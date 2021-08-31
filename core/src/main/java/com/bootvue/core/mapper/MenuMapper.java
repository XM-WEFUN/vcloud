package com.bootvue.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bootvue.core.entity.Menu;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

public interface MenuMapper extends BaseMapper<Menu> {
    List<Menu> findMenuByMenuId(@Param("menus") Set<Long> menus);
}