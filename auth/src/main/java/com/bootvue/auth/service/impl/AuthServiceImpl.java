package com.bootvue.auth.service.impl;

import com.bootvue.auth.service.AuthService;
import com.bootvue.auth.vo.AuthResponse;
import com.bootvue.auth.vo.Credentials;
import com.bootvue.common.config.AppConfig;
import com.bootvue.common.config.Keys;
import com.bootvue.common.result.AppException;
import com.bootvue.common.result.RCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AuthServiceImpl implements AuthService {
    private final AppConfig appConfig;
    private final RedissonClient redissonClient;

    @Override
    public AuthResponse authentication(Credentials credentials) {

        AuthResponse response = new AuthResponse();
        // 0:普通用户名 密码  1:短信登录  2: refresh_token获取新token
        switch (credentials.getType()) {
            case 0:
                response = handleCommonLogin(credentials);
                break;
            case 1:
                response = handleSmsLogin(credentials);
                break;
            case 2:
                response = handleRefreshToken(credentials);
                break;
        }

        return response;
    }

    private AuthResponse handleRefreshToken(Credentials credentials) {
    }

    private AuthResponse handleSmsLogin(Credentials credentials) {
        handleClientKeyCheck(credentials.getAppid(), credentials.getSecret());
    }

    private AuthResponse handleCommonLogin(Credentials credentials) {
        handleClientKeyCheck(credentials.getAppid(), credentials.getSecret());
    }

    private void handleClientKeyCheck(String appid, String secret) {
        // 检查客户端Key
        Keys keys = appConfig.getAuthKey().stream()
                .filter(it -> it.getAppid().equals(appid) && it.getSecret().equals(secret))
                .findAny().orElse(null);

        if (ObjectUtils.isEmpty(keys)) {
            throw new AppException(RCode.PARAM_ERROR.getCode(), "客户端凭证无效");
        }
    }
}
