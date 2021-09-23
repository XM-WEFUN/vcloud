package com.bootvue.auth.service.mapper;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bootvue.auth.mapper.MenuMapper;
import com.bootvue.db.entity.Menu;
import org.springframework.stereotype.Service;

@Service
public class MenuMapperService extends ServiceImpl<MenuMapper, Menu> implements IService<Menu> {
}
