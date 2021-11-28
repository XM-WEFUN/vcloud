package com.bootvue.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bootvue.admin.mapper.RoleMenuMapper;
import com.bootvue.datasource.entity.Menu;
import com.bootvue.datasource.entity.RoleMenu;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RoleMenuMapperService extends ServiceImpl<RoleMenuMapper, RoleMenu> implements IService<RoleMenu> {

    private final RoleMenuMapper roleMenuMapper;

    public Set<Menu> getMenusByRoleIds(Set<Long> roleIds) {
        return roleMenuMapper.getMenusByRoleIds(roleIds);
    }
}
