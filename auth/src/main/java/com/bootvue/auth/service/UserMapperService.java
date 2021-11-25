package com.bootvue.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bootvue.auth.mapper.UserMapper;
import com.bootvue.datasource.entity.User;
import org.springframework.stereotype.Service;

@Service
public class UserMapperService extends ServiceImpl<UserMapper, User> implements IService<User> {
}
