package com.bootvue.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bootvue.db.entity.RoleMenu;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RoleMenuMapper extends BaseMapper<RoleMenu> {
    List<Long> findMenuIdsByRoleIds(@Param("role_ids") List<Long> roleIds);
}