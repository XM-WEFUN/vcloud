package com.bootvue.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bootvue.admin.dto.AssignIn;
import com.bootvue.admin.dto.RoleItem;
import com.bootvue.admin.dto.RoleQueryIn;
import com.bootvue.admin.service.*;
import com.bootvue.common.constant.AppConst;
import com.bootvue.common.model.AppUser;
import com.bootvue.common.result.AppException;
import com.bootvue.common.result.PageOut;
import com.bootvue.common.result.RCode;
import com.bootvue.datasource.entity.*;
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
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class RoleServiceImpl implements RoleService {

    private final RoleMapperService roleMapperService;
    private final RoleMenuMapperService roleMenuMapperService;
    private final UserMapperService userMapperService;
    private final UserRoleMapperService userRoleMapperService;
    private final MenuMapperService menuMapperService;

    @Override
    public PageOut<List<RoleItem>> list(RoleQueryIn param, AppUser user) {
        Long tenantId = AppConst.ADMIN_TENANT_ID.equals(user.getTenantId()) ? (ObjectUtils.isEmpty(param.getTenantId()) ? 0L : param.getTenantId()) : user.getTenantId();

        Page<RoleItem> pages = roleMapperService.roles(new Page<>(param.getCurrent(), param.getPageSize()), param.getName(), tenantId);

        return new PageOut<>(pages.getTotal(), pages.getRecords());
    }

    @Override
    public void addOrUpdate(RoleItem param, AppUser user) {
        Role role;
        if (ObjectUtils.isEmpty(param.getId())) {
            // 新增
            Assert.hasText(param.getName(), "参数错误");
            role = new Role();
        } else {
            // 更新
            role = roleMapperService.getById(param.getId());
            Assert.notNull(role, "参数错误");
            Assert.isTrue(!(AppConst.ADMIN_TENANT_ID.equals(role.getTenantId()) && AppConst.ADMIN_ROLE_NAME.equals(role.getName())), "不可修改");
        }

        if (AppConst.ADMIN_TENANT_ID.equals(user.getTenantId())) {
            role.setTenantId(ObjectUtils.isEmpty(param.getTenantId()) ? user.getTenantId() : param.getTenantId());
        } else {
            role.setTenantId(user.getTenantId());
        }

        if (StringUtils.hasText(param.getName())) {
            // 角色名是否已存在
            Role exist = roleMapperService.getOne(new QueryWrapper<>(new Role().setName(param.getName()).setTenantId(role.getTenantId())));
            Assert.isTrue(ObjectUtils.isEmpty(exist) || exist.getId().equals(param.getId()), "名称已存在");
            role.setName(param.getName());
        }

        role.setAction(StringUtils.hasText(param.getAction()) ? param.getAction() : "");

        roleMapperService.saveOrUpdate(role);
    }

    @Override
    public void delete(Long id, AppUser user) {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Role::getId, id);
        if (!AppConst.ADMIN_TENANT_ID.equals(user.getTenantId())) {
            wrapper.eq(Role::getTenantId, user.getTenantId());
        }
        Role role = roleMapperService.getOne(wrapper);
        Assert.notNull(role, "参数错误");
        if (AppConst.ADMIN_TENANT_ID.equals(user.getTenantId()) && AppConst.ADMIN_ROLE_NAME.equals(role.getName())) {
            throw new AppException(RCode.PARAM_ERROR.getCode(), "不可删除");
        }
        log.info("用户: {} 删除角色: {}", user.getAccount(), role.getName());
        roleMapperService.removeById(role);
    }

    @Override
    public void assignMenu(AssignIn param, AppUser user) {
        // 验证role
        Role role = roleMapperService.getById(param.getId());

        Assert.notNull(role, "参数错误");
        if (!AppConst.ADMIN_TENANT_ID.equals(user.getTenantId())) {
            Assert.isTrue(role.getTenantId().equals(user.getTenantId()), "参数错误");
        }
        // 分配的菜单/按钮id
        Set<Long> ids = param.getIds();

        log.info("角色: {} 分配菜单: {}", role.getName(), ids);
        if (CollectionUtils.isEmpty(ids)) {
            // 清除
            roleMenuMapperService.remove(new QueryWrapper<>(new RoleMenu().setRoleId(param.getId())));
            return;
        }

        // 验证 ids是否都属于 此租户
        Set<Long> menusIds;
        if (!AppConst.ADMIN_TENANT_ID.equals(user.getTenantId())) {
            Role superRole = roleMapperService.getOne(new QueryWrapper<>(new Role().setTenantId(user.getTenantId()).setName(AppConst.ADMIN_ROLE_NAME)));
            menusIds = roleMenuMapperService.list(new QueryWrapper<>(new RoleMenu().setRoleId(superRole.getId()))).stream().map(RoleMenu::getMenuId).collect(Collectors.toSet());
            Assert.isTrue(menusIds.containsAll(ids), "参数错误");
        }

        // 要分配的菜单id --> 需要包含父级id
        ids.addAll(menuMapperService.list(new QueryWrapper<Menu>().lambda().in(Menu::getId, ids)).stream().map(Menu::getPId).filter(e -> !Long.valueOf(0L).equals(e)).collect(Collectors.toSet()));

        // 此角色已有menu id
        menusIds = roleMenuMapperService.list(new QueryWrapper<>(new RoleMenu().setRoleId(param.getId()))).stream().map(RoleMenu::getMenuId).collect(Collectors.toSet());

        // 需要删除的 id
        Sets.SetView<Long> removeIds = Sets.difference(menusIds, ids);
        // 需要新增的 id  需要包含对应的父级id
        Sets.SetView<Long> addIds = Sets.difference(ids, menusIds);

        if (!CollectionUtils.isEmpty(removeIds)) {
            roleMenuMapperService.remove(new QueryWrapper<RoleMenu>().lambda().eq(RoleMenu::getRoleId, param.getId()).in(RoleMenu::getMenuId, removeIds));
        }

        if (!CollectionUtils.isEmpty(addIds)) {
            roleMenuMapperService.saveBatch(addIds.stream().map(e -> new RoleMenu(null, param.getId(), e)).collect(Collectors.toList()));
        }
    }

    @Override
    public void assignUser(AssignIn param, AppUser user) {
        // 验证role
        Role role = roleMapperService.getById(param.getId());

        Assert.notNull(role, "参数错误");
        if (!AppConst.ADMIN_TENANT_ID.equals(user.getTenantId())) {
            Assert.isTrue(role.getTenantId().equals(user.getTenantId()), "参数错误");
        }
        // 分配的用户id
        Set<Long> ids = param.getIds();

        log.info("角色: {} 分配分户: {}", role.getName(), ids);
        if (CollectionUtils.isEmpty(ids)) {
            // 清除
            userRoleMapperService.remove(new QueryWrapper<>(new UserRole().setRoleId(param.getId())));
            return;
        }

        // 验证 ids是否都属于 此租户
        List<User> users = userMapperService.list(new QueryWrapper<User>().lambda().in(User::getId, ids));
        Set<Long> tenants = users.stream().map(User::getTenantId).collect(Collectors.toSet());
        Assert.isTrue(tenants.size() == 1 && tenants.contains(role.getTenantId()), "参数错误");

        // 此角色已有 user id
        Set<Long> orignIds = userRoleMapperService.list(new QueryWrapper<>(new UserRole().setRoleId(param.getId()))).stream().map(UserRole::getUserId).collect(Collectors.toSet());

        // 需要删除的 id
        Sets.SetView<Long> removeIds = Sets.difference(orignIds, ids);
        // 需要新增的 id
        Sets.SetView<Long> addIds = Sets.difference(ids, orignIds);

        if (!CollectionUtils.isEmpty(removeIds)) {
            userRoleMapperService.remove(new QueryWrapper<UserRole>().lambda().eq(UserRole::getRoleId, param.getId()).in(UserRole::getUserId, removeIds));
        }

        if (!CollectionUtils.isEmpty(addIds)) {
            userRoleMapperService.saveBatch(addIds.stream().map(e -> new UserRole(null, e, param.getId())).collect(Collectors.toList()));
        }
    }

    @Override
    public Set<String> listByUser(Long userId, AppUser u) {
        User user = userMapperService.getById(userId);
        Assert.notNull(user, "参数错误");
        if (!AppConst.ADMIN_TENANT_ID.equals(u.getTenantId())) {
            Assert.isTrue(u.getTenantId().equals(user.getTenantId()), "参数错误");
        }

        List<UserRole> roles = userRoleMapperService.list(new QueryWrapper<>(new UserRole().setUserId(userId)));
        return roles.stream().map(e -> String.valueOf(e.getRoleId())).collect(Collectors.toSet());
    }

}
