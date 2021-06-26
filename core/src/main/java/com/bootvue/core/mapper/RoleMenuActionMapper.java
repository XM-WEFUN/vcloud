package com.bootvue.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bootvue.core.entity.RoleMenuAction;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

public interface RoleMenuActionMapper extends BaseMapper<RoleMenuAction> {
    @Delete("delete  from role_menu_action where role_id = #{role_id} ")
    void delByRoleId(@Param("role_id") Long roleId);

    RoleMenuAction findByRoleIdAndMenuKey(@Param("role_id") Long roleId, @Param("key") String key);

    void addRoleMenuActionItem(@Param("role_id") Long roleId, @Param("key") String key, @Param("ids") String ids);

    @Delete("delete from role_menu_action where  menu_id=#{menu_id} ")
    void delByMenuId(@Param("menu_id") Long menu_id);
}