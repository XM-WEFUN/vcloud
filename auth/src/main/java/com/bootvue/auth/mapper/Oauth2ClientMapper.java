package com.bootvue.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bootvue.datasource.entity.Oauth2Client;

public interface Oauth2ClientMapper extends BaseMapper<Oauth2Client> {
    Oauth2Client getClientInfo(String tenantCode, String clientId, String secret, Integer platform);
}
