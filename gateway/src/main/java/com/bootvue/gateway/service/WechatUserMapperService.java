package com.bootvue.gateway.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bootvue.gateway.mapper.WechatUserMapper;
import org.springframework.stereotype.Service;

@Service
public class WechatUserMapperService extends ServiceImpl<WechatUserMapper, WechatUser> implements IService<WechatUser> {
}
