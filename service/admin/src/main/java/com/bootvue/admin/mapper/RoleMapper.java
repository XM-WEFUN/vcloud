package com.bootvue.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bootvue.admin.dto.RoleItem;
import com.bootvue.datasource.entity.Role;
import org.apache.ibatis.annotations.Param;

public interface RoleMapper extends BaseMapper<Role> {
    Page<RoleItem> roles(Page<RoleItem> page, @Param("role_name") String roleName, @Param("tenant_id") Long tenantId);
}
