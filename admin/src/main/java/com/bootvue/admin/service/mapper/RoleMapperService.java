package com.bootvue.admin.service.mapper;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bootvue.admin.mapper.RoleMapper;
import com.bootvue.db.entity.Role;
import org.springframework.stereotype.Service;

@Service
public class RoleMapperService extends ServiceImpl<RoleMapper, Role> implements IService<Role> {

}
