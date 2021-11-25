package com.bootvue.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bootvue.auth.mapper.Oauth2ClientMapper;
import com.bootvue.datasource.entity.Oauth2Client;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class Oauth2ClientMapperService extends ServiceImpl<Oauth2ClientMapper, Oauth2Client> implements IService<Oauth2Client> {

    private final Oauth2ClientMapper oauth2ClientMapper;

    public Oauth2Client getByClientInfo(String tenantCode, String clientId, String secret, Integer platform) {
        return oauth2ClientMapper.getClientInfo(tenantCode, clientId, secret, platform);
    }
}
