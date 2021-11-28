package com.bootvue.gateway.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bootvue.datasource.entity.User;

public interface UserMapper extends BaseMapper<User> {
    User findById(Long id);
}
