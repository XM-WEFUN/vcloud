package com.bootvue.core.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bootvue.core.entity.User;
import com.bootvue.core.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserMapperService {
    private final UserMapper userMapper;

    @Cacheable(cacheNames = "cache:user", key = "#id", unless = "#result == null")
    public User findByIdAndValid(Long id) {
        return userMapper.selectOne(new QueryWrapper<User>().lambda()
                .eq(User::getId, id).eq(User::getStatus, true)
                .isNull(User::getDeleteTime));
    }
}
