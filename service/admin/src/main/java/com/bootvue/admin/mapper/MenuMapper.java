package com.bootvue.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bootvue.admin.dto.MenuItem;
import com.bootvue.datasource.entity.Menu;

import java.util.Set;

public interface MenuMapper extends BaseMapper<Menu> {
    Set<MenuItem> menus();
}
