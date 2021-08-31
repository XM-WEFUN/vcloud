package com.bootvue.core.service;

import com.bootvue.core.dto.RoleMenuDo;
import com.bootvue.core.entity.Menu;
import com.bootvue.core.mapper.MenuMapper;
import com.bootvue.core.mapper.RoleMenuMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MenuMapperService {
    private final RoleMenuMapper roleMenuMapper;
    private final MenuMapper menuMapper;

    public List<RoleMenuDo> findMenuIdByRoleId(List<String> ids) {
        return roleMenuMapper.findMenuIdByRoleId(ids);
    }

    public List<Menu> findMenuByMenuId(Set<Long> menus) {
        return menuMapper.findMenuByMenuId(menus);
    }
}
