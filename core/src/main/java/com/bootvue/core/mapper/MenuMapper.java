package com.bootvue.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bootvue.core.dto.menu.MenuListDo;
import com.bootvue.core.entity.Menu;

public interface MenuMapper extends BaseMapper<Menu> {
    IPage<MenuListDo> listMenu(Page<MenuListDo> page);
}