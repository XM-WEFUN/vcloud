package com.bootvue.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bootvue.admin.dto.RoleIn;
import com.bootvue.admin.dto.RoleQueryIn;
import com.bootvue.admin.dto.RoleQueryOut;
import com.bootvue.core.entity.Role;
import com.bootvue.core.mapper.RoleMapper;
import com.bootvue.core.result.AppException;
import com.bootvue.core.result.PageOut;
import com.bootvue.core.result.RCode;
import com.bootvue.core.service.RoleMenuActionMapperService;
import com.bootvue.core.service.UserMapperService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RoleServiceImpl implements RoleService {
    private final RoleMapper roleMapper;
    private final HttpServletRequest request;
    private final RoleMenuActionMapperService roleMenuActionMapperService;
    private final UserMapperService userMapperService;

    @Override
    public PageOut<List<RoleQueryOut>> roleList(RoleQueryIn param) {
        Page<Role> page = new Page<>(param.getCurrent(), param.getPageSize());
        IPage<Role> roles = roleMapper.findRoles(page, Long.valueOf(request.getHeader("tenant_id")), param.getRoleName());

        PageOut<List<RoleQueryOut>> out = new PageOut<>();
        out.setTotal(roles.getTotal());
        out.setRows(roles.getRecords().stream().map(e -> new RoleQueryOut(e.getId(), e.getName())).collect(Collectors.toList()));
        return out;
    }

    @Override
    public void addOrUpdateRole(RoleIn param) {
        if (!StringUtils.hasText(param.getRoleName())) {
            throw new AppException(RCode.PARAM_ERROR);
        }
        // 角色名是否已存在
        Role existRole = roleMapper.selectOne(new QueryWrapper<Role>().lambda()
                .eq(Role::getName, param.getRoleName())
                .eq(Role::getTenantId, Long.valueOf(request.getHeader("tenant_id")))
        );
        if (!ObjectUtils.isEmpty(existRole)) {
            throw new AppException(RCode.PARAM_ERROR.getCode(), "角色名已存在");
        }

        if (ObjectUtils.isEmpty(param.getId()) || param.getId().equals(0L)) {
            // 新增
            roleMapper.insert(new Role(null, Long.valueOf(request.getHeader("tenant_id")), param.getRoleName()));
        } else {
            // 更新
            Role role = roleMapper.selectById(param.getId());
            if (ObjectUtils.isEmpty(role) || !role.getTenantId().equals(request.getHeader("tenant_id"))) {
                throw new AppException(RCode.PARAM_ERROR);
            }

            role.setName(param.getRoleName());
            roleMapper.updateById(role);
        }
    }

    @Override
    @Transactional
    public void delRole(RoleIn param) {
        // 删除角色  role  role_menu_action  user(role_id)都要删除
        if (ObjectUtils.isEmpty(param.getId())) {
            throw new AppException(RCode.PARAM_ERROR);
        }

        Role role = roleMapper.selectById(param.getId());
        Long tenanId = Long.valueOf(request.getHeader("tenant_id"));
        if (ObjectUtils.isEmpty(role) || !role.getTenantId().equals(tenanId)) {
            throw new AppException(RCode.PARAM_ERROR);
        }

        // role表
        roleMapper.deleteById(role.getId());
        // role_menu_action
        roleMenuActionMapperService.delByRoleId(role.getId());
        // user
        userMapperService.removeUserRoleId(role.getId(), tenanId);
    }
}
