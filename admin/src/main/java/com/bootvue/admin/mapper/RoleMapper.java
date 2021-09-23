package com.bootvue.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bootvue.admin.controller.setting.dto.RoleListOut;
import com.bootvue.db.entity.Role;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RoleMapper extends BaseMapper<Role> {
    IPage<RoleListOut> listRole(Page<Role> page, @Param("name") String name, @Param("tenant_id") Long tenantId);

    List<RoleListOut> listAllRole(@Param("tenant_id") Long tenantId);

    List<Role> listRoleByAdminId(@Param("admin_id") Long adminId);
}