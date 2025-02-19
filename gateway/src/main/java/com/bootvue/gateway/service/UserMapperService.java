package com.bootvue.gateway.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bootvue.common.constant.AppConst;
import com.bootvue.datasource.entity.User;
import com.bootvue.gateway.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserMapperService extends ServiceImpl<UserMapper, User> implements IService<User> {
    private final UserMapper userMapper;

    @Cacheable(cacheNames = AppConst.ADMIN_CACHE, key = "#id", unless = "#result==null")
    public User findById(Long id) {
        return userMapper.findById(id);
    }
}
