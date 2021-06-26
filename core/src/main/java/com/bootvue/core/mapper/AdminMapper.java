package com.bootvue.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bootvue.core.ddo.admin.AdminDo;
import com.bootvue.core.entity.Admin;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.Set;

public interface AdminMapper extends BaseMapper<Admin> {
    @Select("select a.* from `admin` a, tenant t where a.tenant_id = t.id and a.username = #{username} and a.password = #{password} and a.status = 1 and a.role_id > 0 and t.code = #{tenant_code} and a.delete_time is null")
    Admin findByUsernameAndPassword(@Param("username") String username, @Param("password") String password, @Param("tenant_code") String tenantCode);

    @Select("select a.* from `admin` a, tenant t where a.tenant_id = t.id and a.phone = #{phone} and a.status = 1 and a.role_id > 0 and t.code = #{tenant_code} and a.delete_time is null")
    Admin findByPhone(@Param("phone") String phone, @Param("tenant_code") String tenantCode);

    IPage<AdminDo> listAdmins(Page<Admin> page, @Param("username") String username, @Param("tenant_id") Long tenantId);

    @Update("update `admin` set role_id = 0 where role_id = #{role_id} and tenant_id = #{tenant_id} ")
    void removeRoleId(@Param("role_id") Long roleId, @Param("tenant_id") Long tenantId);

    Set<Long> listAdminsByRoleName(@Param("tenant_id") Long tenantId, @Param("role_name") String roleName);

    void batchUpdateRole(@Param("selected_keys") Set<Long> selectedKeys, @Param("role_id") Long roleId, @Param("tenant_id") Long tenantId);

    void batchCancelRole(@Param("un_selected_keys") Set<Long> unSelectedKeys, @Param("role_id") Long roleId, @Param("tenant_id") Long tenantId);

    void batchUpdateStatusByTenantId(@Param("tenant_id") Long tenantId, @Param("status") Integer status);
}