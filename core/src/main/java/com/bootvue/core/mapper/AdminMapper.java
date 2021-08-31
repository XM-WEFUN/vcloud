package com.bootvue.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bootvue.core.entity.Admin;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface AdminMapper extends BaseMapper<Admin> {

    @Select("select a.* from admin a,tenant t where a.phone=#{phone} and a.tenant_id=t.id and t.code=#{tenant_code} and a.status =1 and a.delete_time is null")
    Admin findByPhoneAndTenantCode(@Param("phone") String phone, @Param("tenant_code") String tenantCode);

    @Select("select a.* from admin a ,tenant t where a.username=#{username} and a.password=#{password} and a.tenant_id=t.id and t.code=#{tenant_code}  and a.status=1 and a.delete_time is null")
    Admin findByUsernameAndPassword(@Param("username") String username, @Param("password") String password, @Param("tenant_code") String tenantCode);
}