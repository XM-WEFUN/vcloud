package com.bootvue.admin.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bootvue.admin.dto.RoleItem;
import com.bootvue.admin.mapper.RoleMapper;
import com.bootvue.datasource.entity.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RoleMapperService extends ServiceImpl<RoleMapper, Role> implements IService<Role> {

    private final RoleMapper roleMapper;

    public Page<RoleItem> roles(Page<RoleItem> page, String roleName, Long tenantId) {
        return roleMapper.roles(page, roleName, tenantId);
    }
}
