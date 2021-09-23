package com.bootvue.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bootvue.admin.model.MenuListDo;
import com.bootvue.db.entity.Menu;

public interface MenuMapper extends BaseMapper<Menu> {
    IPage<MenuListDo> listMenu(Page<MenuListDo> page);
}