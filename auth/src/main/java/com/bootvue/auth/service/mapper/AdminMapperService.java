package com.bootvue.auth.service.mapper;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bootvue.auth.mapper.AdminMapper;
import com.bootvue.db.entity.Admin;
import org.springframework.stereotype.Service;

@Service
public class AdminMapperService extends ServiceImpl<AdminMapper, Admin> implements IService<Admin> {
}
