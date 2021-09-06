package com.bootvue.core.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bootvue.core.dto.menu.MenuListDo;
import com.bootvue.core.entity.Menu;
import com.bootvue.core.mapper.MenuMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class MenuMapperService {
    private final MenuMapper menuMapper;

    public IPage<MenuListDo> listMenu(Page<MenuListDo> page) {
        return menuMapper.listMenu(page);
    }

    public List<Menu> listMenuParent() {
        return menuMapper.selectList(new QueryWrapper<Menu>()
                .lambda()
                .eq(Menu::getPId, 0L)
                .eq(Menu::getShow, true)
        );
    }

    public List<Menu> listMenuByIds(List<Long> ids) {
        return menuMapper.selectList(new QueryWrapper<Menu>()
                .lambda()
                .in(Menu::getId, ids)
                .orderByAsc(Menu::getSort)
        );
    }
}
