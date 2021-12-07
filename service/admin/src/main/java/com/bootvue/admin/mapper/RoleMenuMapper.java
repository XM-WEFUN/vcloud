package com.bootvue.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bootvue.admin.dto.MenuItem;
import com.bootvue.datasource.entity.RoleMenu;
import org.apache.ibatis.annotations.Param;

import java.util.Set;

public interface RoleMenuMapper extends BaseMapper<RoleMenu> {
    Set<MenuItem> getMenusByRoleIds(@Param("roleIds") Set<Long> roleIds);

    Set<MenuItem> menus(Long tenantId);
}
