package com.bootvue.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bootvue.admin.dto.MenuItem;
import com.bootvue.admin.mapper.MenuMapper;
import com.bootvue.datasource.entity.Menu;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MenuMapperService extends ServiceImpl<MenuMapper, Menu> implements IService<Menu> {

    private final MenuMapper menuMapper;

    public Set<MenuItem> menus() {
        return menuMapper.menus();
    }
}
