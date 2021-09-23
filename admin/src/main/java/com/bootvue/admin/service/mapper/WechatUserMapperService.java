package com.bootvue.admin.service.mapper;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bootvue.admin.mapper.WechatUserMapper;
import com.bootvue.db.entity.WechatUser;
import org.springframework.stereotype.Service;

@Service
public class WechatUserMapperService extends ServiceImpl<WechatUserMapper, WechatUser> implements IService<WechatUser> {

}
