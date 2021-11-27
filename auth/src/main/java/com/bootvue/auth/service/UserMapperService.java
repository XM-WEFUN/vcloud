package com.bootvue.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bootvue.auth.mapper.UserMapper;
import com.bootvue.datasource.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserMapperService extends ServiceImpl<UserMapper, User> implements IService<User> {

    private final UserMapper userMapper;

    public User getByTenantCodeAndAccount(String tenantCode, String account, String password) {
        return userMapper.getByTenantCodeAndAccount(tenantCode, account, password);
    }
}
