package com.bootvue.auth.service.mapper;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bootvue.auth.mapper.RoleAdminMapper;
import com.bootvue.db.entity.RoleAdmin;
import org.springframework.stereotype.Service;

@Service
public class RoleAdminMapperService extends ServiceImpl<RoleAdminMapper, RoleAdmin> implements IService<RoleAdmin> {
}
