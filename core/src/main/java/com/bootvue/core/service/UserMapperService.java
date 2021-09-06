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

    public User findById(Long id) {
        return userMapper.selectOne(new QueryWrapper<User>()
                .lambda()
                .eq(User::getId, id)
                .eq(User::getStatus, true)
        );
    }

    public User findByOpenid(String openid) {
        return userMapper.selectOne(new QueryWrapper<User>()
                .lambda()
                .eq(User::getOpenid, openid)
        );
    }

    public void updateUser(User user) {
        userMapper.updateById(user);
    }
}
