package com.bootvue.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bootvue.core.entity.RoleMenuAction;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;

public interface RoleMenuActionMapper extends BaseMapper<RoleMenuAction> {
    @Delete("delete from role_menu_action where role_id = #{role_id} ")
    void delByRoleId(@Param("role_id") Long roleId);
}