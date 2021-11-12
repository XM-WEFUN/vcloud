package com.bootvue.gateway.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bootvue.gateway.mapper.AdminMapper;
import org.springframework.stereotype.Service;

@Service
public class AdminMapperService extends ServiceImpl<AdminMapper, Admin> implements IService<Admin> {
}
