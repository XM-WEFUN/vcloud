package com.bootvue.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bootvue.admin.controller.setting.dto.*;
import com.bootvue.admin.mapper.RoleMapper;
import com.bootvue.admin.service.RoleService;
import com.bootvue.admin.service.mapper.AdminMapperService;
import com.bootvue.admin.service.mapper.RoleAdminMapperService;
import com.bootvue.admin.service.mapper.RoleMapperService;
import com.bootvue.admin.service.mapper.RoleMenuMapperService;
import com.bootvue.core.constant.AppConst;
import com.bootvue.core.model.AppUser;
import com.bootvue.core.result.AppException;
import com.bootvue.core.result.PageOut;
import com.bootvue.core.result.RCode;
import com.bootvue.db.entity.Admin;
import com.bootvue.db.entity.Role;
import com.bootvue.db.entity.RoleAdmin;
import com.bootvue.db.entity.RoleMenu;
import com.google.common.collect.Sets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RoleServiceImpl implements RoleService {
    private final RoleMapper roleMapper;
    private final RoleMapperService roleMapperService;
    private final AdminMapperService adminMapperService;
    private final RoleMenuMapperService roleMenuMapperService;
    private final RoleAdminMapperService roleAdminMapperService;

    @Override
    public PageOut<List<RoleListOut>> listRole(RoleListIn param, AppUser user) {
        Page<Role> page = new Page<>(param.getCurrent(), param.getPageSize());

        IPage<RoleListOut> roles = roleMapper.listRole(page, param.getRoleName(), AppConst.ADMIN_TENANT_ID.equals(user.getTenantId()) ? null : user.getTenantId());

        PageOut<List<RoleListOut>> out = new PageOut<>();
        out.setTotal(roles.getTotal());
        out.setRows(roles.getRecords());

        return out;
    }

    @Override
    public void addOrUpdateRole(RoleIn param, AppUser user) {
        if (!AppConst.ADMIN_TENANT_ID.equals(user.getTenantId()) && !user.getTenantId().equals(param.getTenantId())) {
            throw new AppException(RCode.ACCESS_DENY);
        }

        if (!StringUtils.hasText(param.getName()) || "超级管理员".equals(param.getName())) {
            throw new AppException(RCode.PARAM_ERROR);
        }

        if (param.getId() != null && param.getId().compareTo(0L) > 0) {
            roleMapperService.updateById(new Role().setId(param.getId()).setName(param.getName()));
        } else {
            Role role = roleMapperService.getOne(new QueryWrapper<Role>()
                    .lambda().eq(Role::getName, param.getName())
            );

            Assert.isNull(role, "名称已存在");

            roleMapper.insert(new Role(null, param.getTenantId(), param.getName()));
        }
    }

    @Override
    public void deleteRole(RoleIn param, AppUser user) {
        if (ObjectUtils.isEmpty(param.getId()) || ObjectUtils.isEmpty(param.getTenantId()) || !(param.getId().compareTo(0L) > 0)) {
            throw new AppException(RCode.PARAM_ERROR);
        }

        if (!AppConst.ADMIN_TENANT_ID.equals(user.getTenantId()) && !user.getTenantId().equals(param.getTenantId())) {
            throw new AppException(RCode.ACCESS_DENY);
        }

        // 删除角色  role role_menu role_admin
        roleMapperService.removeById(param.getId());

        roleAdminMapperService.remove(new QueryWrapper<>(new RoleAdmin().setRoleId(param.getId())));

        roleMenuMapperService.remove(new QueryWrapper<RoleMenu>()
                .lambda()
                .eq(RoleMenu::getRoleId, param.getId())
        );

    }

    @Override
    public void assignUser(RoleAdminIn param, AppUser user) {
        Long roleId = param.getRoleId();
        Set<Long> ids = param.getIds();
        Role role = roleMapperService.getById(roleId);
        Assert.notNull(role, "参数错误");

        if (!AppConst.ADMIN_TENANT_ID.equals(user.getTenantId()) && !role.getTenantId().equals(user.getTenantId())) {
            throw new AppException(RCode.ACCESS_DENY);
        }

        // 为某个角色分配/取消分配用户
        log.info("角色id: {} 分配用户id: {}", roleId, ids);
        if (CollectionUtils.isEmpty(param.getIds())) {
            // 删除此角色对应的所有用户
            roleAdminMapperService.remove(new QueryWrapper<>(new RoleAdmin().setRoleId(roleId)));
            return;
        }

        // 此角色原有用户
        List<RoleAdmin> userRoles = roleAdminMapperService.list(new QueryWrapper<>(new RoleAdmin().setRoleId(roleId)));
        if (CollectionUtils.isEmpty(userRoles)) {
            roleAdminMapperService.saveBatch(ids.stream().map(e -> new RoleAdmin(null, roleId, e)).collect(Collectors.toList()));
            return;
        }

        // 要新增/删除的用户id
        // 原有用户id
        Set<Long> orignUserIds = userRoles.stream().map(e -> e.getAdminId()).collect(Collectors.toSet());

        // 不能存在的新增
        roleAdminMapperService.saveBatch(ids.stream().filter(e -> !orignUserIds.contains(e)).map(i -> new RoleAdmin(null, roleId, i)).collect(Collectors.toList()));

        // 已分配的用户  现在取消了的
        roleAdminMapperService.remove(new QueryWrapper<RoleAdmin>().lambda()
                .eq(RoleAdmin::getRoleId, roleId)
                .in(RoleAdmin::getAdminId, orignUserIds.stream().filter(i -> !ids.contains(i)).collect(Collectors.toSet())));
    }

    @Override
    public void assignMenu(RoleMenuIn param, AppUser user) {
        Long roleId = param.getRoleId();
        Set<Long> ids = param.getIds();

        Role role = roleMapperService.getById(roleId);
        Assert.notNull(role, "参数错误");

        if (!AppConst.ADMIN_TENANT_ID.equals(user.getTenantId()) && !role.getTenantId().equals(user.getTenantId())) {
            throw new AppException(RCode.ACCESS_DENY);
        }

        // 为某个角色分配/取消分配 菜单
        log.info("角色id: {} 分配菜单id: {}", roleId, ids);

        // 此租户超级管理员  ---> 所有菜单id
        if (!AppConst.ADMIN_TENANT_ID.equals(user.getTenantId())) {
            Role superRole = roleMapperService.getOne(new QueryWrapper<>(new Role().setName("超级管理员").setTenantId(user.getTenantId())));
            if (ObjectUtils.isEmpty(superRole)) {
                throw new AppException(RCode.ACCESS_DENY);
            }
            List<RoleMenu> menus = roleMenuMapperService.list(new QueryWrapper<>(new RoleMenu().setRoleId(superRole.getId())));
            Set<Long> allMenuIds = menus.stream().map(i -> i.getMenuId()).collect(Collectors.toSet());
            Assert.isTrue(allMenuIds.containsAll(ids), "参数错误");
        }

        if (CollectionUtils.isEmpty(ids)) {
            // 删除此角色对应的所有菜单
            roleMenuMapperService.remove(new QueryWrapper<>(new RoleMenu().setRoleId(roleId)));
            return;
        }

        // 此角色原有 菜单
        List<RoleMenu> roleMenus = roleMenuMapperService.list(new QueryWrapper<>(new RoleMenu().setRoleId(roleId)));

        // 要新增/删除的菜单id
        // 原有菜单id
        Set<Long> orignMenuIds = roleMenus.stream().map(e -> e.getMenuId()).collect(Collectors.toSet());

        // 需要剔除的
        Sets.SetView<Long> removeIds = Sets.difference(orignMenuIds, ids);
        if (removeIds.size() > 0) {
            roleMenuMapperService.remove(new QueryWrapper<RoleMenu>().lambda()
                    .eq(RoleMenu::getRoleId, roleId)
                    .in(RoleMenu::getMenuId, removeIds)
            );
        }

        // 不存在的新增
        Sets.SetView<Long> addIds = Sets.difference(ids, orignMenuIds);
        if (addIds.size() > 0) {
            roleMenuMapperService.saveBatch(addIds.stream().map(i -> new RoleMenu(null, roleId, i)).collect(Collectors.toList()));
        }
    }

    @Override
    public List<RoleListOut> listAllRole(AppUser user) {
        return roleMapper.listAllRole(AppConst.ADMIN_TENANT_ID.equals(user.getTenantId()) ? null : user.getTenantId());
    }

    @Override
    public List<String> listRoleIdByAdminId(AdminIn param, AppUser user) {
        Admin admin = adminMapperService.getById(param.getId());
        Assert.notNull(admin, "参数错误");
        if (!AppConst.ADMIN_TENANT_ID.equals(user.getTenantId()) && admin.getTenantId().equals(user.getTenantId())) {
            throw new AppException(RCode.ACCESS_DENY);
        }

        return roleAdminMapperService.list(new QueryWrapper<>(new RoleAdmin().setAdminId(param.getId())))
                .stream().map(e -> String.valueOf(e.getRoleId())).collect(Collectors.toList());
    }
}
