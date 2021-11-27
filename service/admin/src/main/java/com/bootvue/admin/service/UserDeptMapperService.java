package com.bootvue.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bootvue.admin.mapper.UserDeptMapper;
import com.bootvue.datasource.entity.UserDept;
import org.springframework.stereotype.Service;

@Service
public class UserDeptMapperService extends ServiceImpl<UserDeptMapper, UserDept> implements IService<UserDept> {
}
