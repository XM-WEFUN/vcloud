package com.bootvue.core.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bootvue.core.ddo.user.UserDo;
import com.bootvue.core.entity.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface UserMapper extends BaseMapper<User> {
    @Select("select u.* from `user` u, tenant t where u.tenant_id = t.id and u.username = #{username} and u.password = #{password} and u.status = 1 and u.role_id > 0 and t.code = #{tenant_code} and u.delete_time is null")
    User findByUsernameAndPassword(@Param("username") String username, @Param("password") String password, @Param("tenant_code") String tenantCode);

    @Select("select u.* from `user` u, tenant t where u.tenant_id = t.id and u.phone = #{phone} and u.status = 1 and u.role_id > 0 and t.code = #{tenant_code} and u.delete_time is null")
    User findByPhone(@Param("phone") String phone, @Param("tenant_code") String tenantCode);

    @Select("select u.* from `user` u, tenant t where u.tenant_id = t.id and u.openid=#{openid} and u.status=1 and u.role_id = -1 and t.code=#{tenant_code} and u.delete_time is null")
    User findByOpenid(@Param("openid") String openid, @Param("tenant_code") String tenantCode);

    IPage<UserDo> listUsers(Page<User> page, @Param("username") String username, @Param("tenantId") Long tenantId);

    @Update("update `user` set role_id = 0 where role_id = #{role_id} and tenant_id = #{tenant_id} ")
    void removeRoleId(@Param("role_id") Long roleId, @Param("tenant_id") Long tenantId);
}