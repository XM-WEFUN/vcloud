package com.bootvue.admin.service.mapper;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bootvue.admin.mapper.TenantMapper;
import com.bootvue.db.entity.Tenant;
import org.springframework.stereotype.Service;

@Service
public class TenantMapperService extends ServiceImpl<TenantMapper, Tenant> implements IService<Tenant> {

}
