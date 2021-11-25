package com.bootvue.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bootvue.auth.mapper.UserRoleMapper;
import com.bootvue.datasource.entity.UserRole;
import org.springframework.stereotype.Service;

@Service
public class UserRoleMapperService extends ServiceImpl<UserRoleMapper, UserRole> implements IService<UserRole> {
}
