package com.bootvue.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bootvue.datasource.entity.User;

public interface UserMapper extends BaseMapper<User> {
    User getByTenantCodeAndAccount(String tenantCode, String account, String password);
}
