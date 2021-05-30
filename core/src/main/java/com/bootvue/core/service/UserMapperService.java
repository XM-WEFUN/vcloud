package com.bootvue.core.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bootvue.core.entity.User;
import com.bootvue.core.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserMapperService {

    private final UserMapper userMapper;

    public User findByOpenid(String openid, String tenantCode) {
        return userMapper.selectOne(new QueryWrapper<User>().lambda().eq(User::getOpenid, openid).eq(User::getTenantId, tenantCode));
    }
}
