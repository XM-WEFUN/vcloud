package com.bootvue.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bootvue.core.ddo.role.RoleDo;
import com.bootvue.core.entity.Role;
import org.apache.ibatis.annotations.Param;

public interface RoleMapper extends BaseMapper<Role> {
    IPage<RoleDo> findRoles(Page<Role> page, @Param("tenant_id") Long tenantId, @Param("role_name") String roleName, @Param("tid") Long tid);
}