package com.bootvue.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bootvue.auth.mapper.TenantMapper;
import com.bootvue.datasource.entity.Tenant;
import org.springframework.stereotype.Service;

@Service
public class TenantMapperService extends ServiceImpl<TenantMapper, Tenant> implements IService<Tenant> {
}
