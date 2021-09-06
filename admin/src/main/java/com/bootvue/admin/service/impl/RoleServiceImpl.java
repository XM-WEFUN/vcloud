package com.bootvue.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bootvue.admin.controller.setting.dto.*;
import com.bootvue.admin.service.RoleService;
import com.bootvue.core.entity.Role;
import com.bootvue.core.entity.RoleAdmin;
import com.bootvue.core.entity.RoleMenu;
import com.bootvue.core.mapper.RoleAdminMapper;
import com.bootvue.core.mapper.RoleMapper;
import com.bootvue.core.mapper.RoleMenuMapper;
import com.bootvue.core.result.AppException;
import com.bootvue.core.result.PageOut;
import com.bootvue.core.result.RCode;
import com.bootvue.core.service.RoleMapperService;
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
    private final RoleMapperService roleMapperService;
    private final RoleMapper roleMapper;
    private final RoleAdminMapper roleAdminMapper;
    private final RoleMenuMapper roleMenuMapper;

    @Override
    public PageOut<List<RoleListOut>> listRole(RoleListIn param) {
        Page<Role> page = new Page<>(param.getCurrent(), param.getPageSize());
        IPage<Role> roles = roleMapperService.listRole(page, param.getName());
        PageOut<List<RoleListOut>> out = new PageOut<>();
        out.setTotal(roles.getTotal());

        out.setRows(roles.getRecords().stream().map(e -> new RoleListOut(e.getId(), e.getName())).collect(Collectors.toList()));

        return out;
    }

    @Override
    public void addOrUpdateRole(RoleIn param) {
        if (!StringUtils.hasText(param.getName())) {
            throw new AppException(RCode.PARAM_ERROR);
        }

        if (param.getId() != null && param.getId().compareTo(0L) > 0) {
            roleMapper.updateById(new Role(param.getId(), param.getName()));
        } else {
            Role role = roleMapper.selectOne(new QueryWrapper<Role>()
                    .lambda().eq(Role::getName, param.getName())
            );

            Assert.isNull(role, "名称已存在");

            roleMapper.insert(new Role(null, param.getName()));
        }
    }

    @Override
    public void deleteRole(RoleIn param) {
        if (ObjectUtils.isEmpty(param.getId()) || !(param.getId().compareTo(0L) > 0)) {
            throw new AppException(RCode.PARAM_ERROR);
        }
        // 删除角色  role_admin role_menu
        roleMapper.deleteById(param.getId());
        roleAdminMapper.delete(new QueryWrapper<RoleAdmin>()
                .lambda()
                .eq(RoleAdmin::getRoleId, param.getId())
        );
        roleMenuMapper.delete(new QueryWrapper<RoleMenu>()
                .lambda()
                .eq(RoleMenu::getRoleId, param.getId())
        );

    }

    @Override
    public void assignUser(RoleAdminIn param) {
        Long roleId = param.getRoleId();
        Set<Long> ids = param.getIds();

        // 为某个角色分配/取消分配用户
        log.info("角色id: {} 分配用户: {}", roleId, ids);
        if (CollectionUtils.isEmpty(param.getIds())) {
            // 删除此角色对应的所有用户
            roleAdminMapper.delete(new QueryWrapper<RoleAdmin>().lambda().eq(RoleAdmin::getRoleId, roleId));
            return;
        }

        // 此角色原有用户
        List<RoleAdmin> userRoles = roleAdminMapper.selectList(new QueryWrapper<RoleAdmin>().lambda().eq(RoleAdmin::getRoleId, roleId));
        if (CollectionUtils.isEmpty(userRoles)) {
            ids.forEach(e -> roleAdminMapper.insert(new RoleAdmin(null, roleId, e)));
            return;
        }

        // 要新增/删除的用户id
        // 原有用户id
        Set<Long> orignUserIds = userRoles.stream().map(e -> e.getAdminId()).collect(Collectors.toSet());

        ids.forEach(e -> {
            if (!orignUserIds.contains(e)) {
                // 新增
                roleAdminMapper.insert(new RoleAdmin(null, roleId, e));
            }
        });

        orignUserIds.forEach(e -> {
            if (!ids.contains(e)) {
                // 删除
                roleAdminMapper.delete(new QueryWrapper<RoleAdmin>().lambda().eq(RoleAdmin::getAdminId, e).eq(RoleAdmin::getRoleId, roleId));
            }
        });
    }

    @Override
    public void assignMenu(RoleMenuIn param) {
        Long roleId = param.getRoleId();
        Set<Long> ids = param.getIds();

        // 为某个角色分配/取消分配 菜单
        log.info("角色id: {} 分配菜单: {}", roleId, ids);
        if (CollectionUtils.isEmpty(ids)) {
            // 删除此角色对应的所有菜单
            roleMenuMapper.delete(new QueryWrapper<RoleMenu>().lambda().eq(RoleMenu::getRoleId, roleId));
            return;
        }

        // 此角色原有 菜单
        List<RoleMenu> roleMenus = roleMenuMapper.selectList(new QueryWrapper<RoleMenu>().lambda().eq(RoleMenu::getRoleId, roleId));
        if (CollectionUtils.isEmpty(roleMenus)) {
            ids.forEach(e -> roleMenuMapper.insert(new RoleMenu(null, roleId, e)));
            return;
        }

        // 要新增/删除的菜单id
        // 原有菜单id
        Set<Long> orignMenuIds = roleMenus.stream().map(e -> e.getMenuId()).collect(Collectors.toSet());

        ids.forEach(e -> {
            if (!orignMenuIds.contains(e)) {
                // 新增
                roleMenuMapper.insert(new RoleMenu(null, roleId, e));
            }
        });

        orignMenuIds.forEach(e -> {
            if (!ids.contains(e)) {
                // 删除
                roleMenuMapper.delete(new QueryWrapper<RoleMenu>().lambda().eq(RoleMenu::getMenuId, e).eq(RoleMenu::getRoleId, roleId));
            }
        });
    }

    @Override
    public List<RoleListOut> listAllRole() {
        List<Role> roles = roleMapper.selectList(new QueryWrapper<>());

        return roles.stream().map(e -> new RoleListOut(e.getId(), e.getName())).collect(Collectors.toList());
    }

    @Override
    public List<Long> listRoleIdByAdminId(AdminIn param) {
        List<RoleAdmin> roleAdmins = roleAdminMapper.selectList(new QueryWrapper<RoleAdmin>()
                .lambda()
                .eq(RoleAdmin::getAdminId, param.getId())
        );

        return roleAdmins.stream().map(e -> e.getRoleId()).collect(Collectors.toList());
    }
}
