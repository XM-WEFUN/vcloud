package com.bootvue.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bootvue.admin.dto.RoleIn;
import com.bootvue.admin.dto.RoleQueryIn;
import com.bootvue.admin.dto.RoleQueryOut;
import com.bootvue.admin.service.RoleService;
import com.bootvue.core.constant.AppConst;
import com.bootvue.core.ddo.role.RoleDo;
import com.bootvue.core.entity.Role;
import com.bootvue.core.mapper.RoleMapper;
import com.bootvue.core.result.AppException;
import com.bootvue.core.result.PageOut;
import com.bootvue.core.result.RCode;
import com.bootvue.core.service.AdminMapperService;
import com.bootvue.core.service.RoleMenuActionMapperService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RoleServiceImpl implements RoleService {
    private final RoleMapper roleMapper;
    private final HttpServletRequest request;
    private final RoleMenuActionMapperService roleMenuActionMapperService;
    private final AdminMapperService adminMapperService;

    @Override
    public PageOut<List<RoleQueryOut>> roleList(RoleQueryIn param) {
        Page<Role> page = new Page<>(param.getCurrent(), param.getPageSize());
        IPage<RoleDo> roles = roleMapper.findRoles(page, Long.valueOf(request.getHeader(AppConst.HEADER_TENANT_ID)), param.getRoleName());

        PageOut<List<RoleQueryOut>> out = new PageOut<>();
        out.setTotal(roles.getTotal());
        out.setRows(roles.getRecords().stream().map(e -> new RoleQueryOut(e.getId(), e.getRoleName(), e.getTenantName())).collect(Collectors.toList()));
        return out;
    }

    @Override
    public void addOrUpdateRole(RoleIn param) {
        Assert.notNull(param.getRoleName(), RCode.PARAM_ERROR.getMsg());
        // 角色名是否已存在
        Long tenantId = Long.valueOf(request.getHeader(AppConst.HEADER_TENANT_ID));
        Role existRole = roleMapper.selectOne(new QueryWrapper<Role>().lambda()
                .eq(Role::getName, param.getRoleName())
                .eq(Role::getTenantId, tenantId)
        );
        if (!ObjectUtils.isEmpty(existRole)) {
            throw new AppException(RCode.PARAM_ERROR.getCode(), "角色名已存在");
        }

        if (ObjectUtils.isEmpty(param.getId()) || param.getId().equals(0L)) {
            // 新增
            roleMapper.insert(new Role(null, tenantId, param.getRoleName()));
        } else {
            // 更新
            Role role = roleMapper.selectById(param.getId());
            if (ObjectUtils.isEmpty(role) || !role.getTenantId().equals(tenantId)) {
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
        Assert.notNull(param.getId(), RCode.PARAM_ERROR.getMsg());

        Role role = roleMapper.selectById(param.getId());
        Long tenanId = Long.valueOf(request.getHeader(AppConst.HEADER_TENANT_ID));
        if (ObjectUtils.isEmpty(role) || !role.getTenantId().equals(tenanId)) {
            throw new AppException(RCode.PARAM_ERROR);
        }

        // role表
        roleMapper.deleteById(role.getId());
        // role_menu_action
        roleMenuActionMapperService.delByRoleId(role.getId());
        // user
        adminMapperService.removeUserRoleId(role.getId(), tenanId);
    }
}
