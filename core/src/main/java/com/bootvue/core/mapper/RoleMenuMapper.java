package com.bootvue.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bootvue.core.entity.RoleMenu;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RoleMenuMapper extends BaseMapper<RoleMenu> {
    List<Long> findMenuIdsByAdminId(@Param("admin_id") Long adminId);
}