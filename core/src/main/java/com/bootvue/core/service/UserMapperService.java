package com.bootvue.core.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bootvue.core.constant.AppConst;
import com.bootvue.core.entity.User;
import com.bootvue.core.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserMapperService {

    private final UserMapper userMapper;

    public User findByOpenid(String openid, Long tenantId) {
        return userMapper.selectOne(new QueryWrapper<User>().lambda().eq(User::getOpenid, openid).eq(User::getTenantId, tenantId));
    }

    @Cacheable(cacheNames = AppConst.USER_CACHE, key = "#id", unless = "#result == null")
    public User findById(Long id) {
        return userMapper.selectById(id);
    }

    @CacheEvict(cacheNames = AppConst.USER_CACHE, key = "#user.id")
    public int updateUser(User user) {
        return userMapper.updateById(user);
    }
}
