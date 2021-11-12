package com.bootvue.gateway.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bootvue.common.constant.AppConst;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AuthService {
    private final AdminMapperService adminMapperService;
    private final WechatUserMapperService wechatUserMapperService;

    @Cacheable(cacheNames = AppConst.ADMIN_CACHE, key = "#id", unless = "#result==null")
    public Admin findByAdminId(Long id) {
        return adminMapperService.getOne(new QueryWrapper<Admin>()
                .lambda()
                .eq(Admin::getId, id)
                .eq(Admin::getStatus, true)
                .isNull(Admin::getDeleteTime));
    }

    @Cacheable(cacheNames = AppConst.WECHAT_USER_CACHE, key = "#id", unless = "#result==null")
    public WechatUser findByUserId(Long id) {
        return wechatUserMapperService.getOne(new QueryWrapper<WechatUser>()
                .lambda()
                .eq(WechatUser::getId, id)
                .eq(WechatUser::getStatus, true)
                .isNull(WechatUser::getDeleteTime));
    }
}
