package com.bootvue.auth.service.mapper;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bootvue.auth.mapper.WechatUserMapper;
import com.bootvue.db.entity.WechatUser;
import org.springframework.stereotype.Service;

@Service
public class WechatUserMapperService extends ServiceImpl<WechatUserMapper, WechatUser> implements IService<WechatUser> {
}
