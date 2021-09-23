package com.bootvue.admin.service.mapper;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bootvue.admin.mapper.RoleMenuMapper;
import com.bootvue.db.entity.RoleMenu;
import org.springframework.stereotype.Service;

@Service
public class RoleMenuMapperService extends ServiceImpl<RoleMenuMapper, RoleMenu> implements IService<RoleMenu> {

}
