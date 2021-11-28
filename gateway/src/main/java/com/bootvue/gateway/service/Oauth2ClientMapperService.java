package com.bootvue.gateway.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bootvue.common.constant.AppConst;
import com.bootvue.datasource.entity.Oauth2Client;
import com.bootvue.gateway.mapper.Oauth2ClientMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class Oauth2ClientMapperService extends ServiceImpl<Oauth2ClientMapper, Oauth2Client> implements IService<Oauth2Client> {

    @Cacheable(cacheNames = AppConst.OAUTH2_CACHE, key = "#id", unless = "#result==null")
    public Oauth2Client findById(Long id) {
        return getById(id);
    }
}
