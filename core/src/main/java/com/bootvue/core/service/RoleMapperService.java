package com.bootvue.core.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bootvue.core.entity.Role;
import com.bootvue.core.mapper.RoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RoleMapperService {
    private final RoleMapper roleMapper;

    public Role findRoleByIdAndTenantId(Long roleId, Long tenantId) {
        return roleMapper.selectOne(new QueryWrapper<Role>().lambda()
                .eq(Role::getId, roleId).eq(Role::getTenantId, tenantId)
        );
    }


    public Role findRoleByNameAndTenantId(String name, Long tenantId) {
        return roleMapper.selectOne(new QueryWrapper<Role>().lambda()
                .eq(Role::getName, name).eq(Role::getTenantId, tenantId)
        );
    }

}
