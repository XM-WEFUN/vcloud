package com.bootvue.admin.service.mapper;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bootvue.admin.mapper.RoleAdminMapper;
import com.bootvue.db.entity.RoleAdmin;
import org.springframework.stereotype.Service;

@Service
public class RoleAdminMapperService extends ServiceImpl<RoleAdminMapper, RoleAdmin> implements IService<RoleAdmin> {

}
