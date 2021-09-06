package com.bootvue.core.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bootvue.core.entity.Role;
import com.bootvue.core.mapper.RoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RoleMapperService {
    private final RoleMapper roleMapper;

    public IPage<Role> listRole(Page<Role> page, String name) {
        return roleMapper.listRole(page, name);
    }
}
