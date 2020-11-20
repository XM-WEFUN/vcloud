package com.bootvue.auth.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bootvue.auth.service.AuthService;
import com.bootvue.auth.vo.AuthResponse;
import com.bootvue.auth.vo.Credentials;
import com.bootvue.core.config.app.AppConfig;
import com.bootvue.core.constant.AppConst;
import com.bootvue.core.entity.User;
import com.bootvue.core.mapper.UserMapper;
import com.bootvue.core.module.Token;
import com.bootvue.core.result.AppException;
import com.bootvue.core.result.RCode;
import com.bootvue.core.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.redisson.api.RBucket;
import org.redisson.api.RSetCache;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AuthServiceImpl implements AuthService {
    private final AppConfig appConfig;
    private final RedissonClient redissonClient;
    private final UserMapper userMapper;

    @Override
    public AuthResponse authentication(Credentials credentials) {

        AuthResponse response = null;
        // 0:普通用户名 密码  1:短信登录  2: refresh_token获取新token
        switch (credentials.getType()) {
            case 0:
                response = handleCommonLogin(credentials, response);
                break;
            case 1:
                response = handleSmsLogin(credentials, response);
                break;
            case 2:
                response = handleRefreshToken(credentials, response);
                break;
        }

        return response;
    }

    private AuthResponse handleRefreshToken(Credentials credentials, AuthResponse response) {
        return null;
    }

    private AuthResponse handleSmsLogin(Credentials credentials, AuthResponse response) {
        return null;
    }

    private AuthResponse handleCommonLogin(Credentials credentials, AuthResponse response) {

        if (StringUtils.isEmpty(credentials.getKey()) || StringUtils.isEmpty(credentials.getUsername()) ||
                StringUtils.isEmpty(credentials.getPassword()) || StringUtils.isEmpty(credentials.getCode())) {
            throw new AppException(RCode.PARAM_ERROR);
        }

        // 校验验证码
        RBucket<String> bucket = redissonClient.getBucket(String.format(AppConst.CAPTCHA_KEY, credentials.getKey()));
        String storedCode = bucket.getAndDelete();
        if (StringUtils.isEmpty(storedCode) || !credentials.getCode().equalsIgnoreCase(storedCode)) {
            throw new AppException(RCode.PARAM_ERROR.getCode(), "验证码无效");
        }

        // 验证 用户名 密码
        User user = userMapper.selectOne(new QueryWrapper<User>()
                .lambda()
                .eq(User::getUsername, credentials.getUsername()).eq(User::getTenantCode, credentials.getTenantCode())
                .eq(User::getPassword, DigestUtils.md5Hex(credentials.getPassword())));

        if (ObjectUtils.isEmpty(user)) {
            throw new AppException(RCode.PARAM_ERROR.getCode(), "用户信息错误");
        }

        // 响应token信息
        Token accessToken = new Token();
        Token refreshToken = new Token();
        BeanUtils.copyProperties(user, accessToken);
        BeanUtils.copyProperties(user, refreshToken);
        accessToken.setUserId(user.getId());
        accessToken.setType(AppConst.ACCESS_TOKEN);
        refreshToken.setUserId(user.getId());
        refreshToken.setType(AppConst.REFRESH_TOKEN);

        response = new AuthResponse();
        BeanUtils.copyProperties(user, response);
        response.setUserId(user.getId());

        String accessTokenStr = JwtUtil.encode(LocalDateTime.now().plusSeconds(7200L), BeanUtil.beanToMap(accessToken));
        String refreshTokenStr = JwtUtil.encode(LocalDateTime.now().plusDays(7), BeanUtil.beanToMap(refreshToken));

        response.setAccessToken(accessTokenStr);
        response.setRefreshToken(refreshTokenStr);
        response.setExpires(7200L);

        // redis 保存token信息 (可能用不到  看实时控制要求高不高)
        RSetCache<String> aSetCache = redissonClient.getSetCache(String.format(AppConst.ACCESS_TOKEN_KEY, user.getId()));
        RSetCache<String> rSetCache = redissonClient.getSetCache(String.format(AppConst.REFRESH_TOKEN_KEY, user.getId()));
        aSetCache.add(accessTokenStr, 7200L, TimeUnit.SECONDS);
        rSetCache.add(accessTokenStr, 7L, TimeUnit.DAYS);

        return response;
    }

}
