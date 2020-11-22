package com.bootvue.auth.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bootvue.auth.service.AuthService;
import com.bootvue.auth.vo.AuthResponse;
import com.bootvue.auth.vo.Credentials;
import com.bootvue.core.constant.AppConst;
import com.bootvue.core.entity.User;
import com.bootvue.core.mapper.UserMapper;
import com.bootvue.core.module.Token;
import com.bootvue.core.result.AppException;
import com.bootvue.core.result.RCode;
import com.bootvue.core.service.UserMapperService;
import com.bootvue.core.util.JwtUtil;
import io.jsonwebtoken.Claims;
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
    private final RedissonClient redissonClient;
    private final UserMapper userMapper;
    private final UserMapperService userMapperService;

    @Override
    public AuthResponse authentication(Credentials credentials) {

        // 0:普通用户名 密码  1:短信登录  2: refresh_token获取新token
        switch (credentials.getType()) {
            case 0:
                return handleCommonLogin(credentials);
            case 1:
                return handleSmsLogin(credentials);
            case 2:
                return handleRefreshToken(credentials);
            default:
                throw new AppException(RCode.PARAM_ERROR.getCode(), "未知认证类型");
        }
    }

    /**
     * 换取新的access_token
     *
     * @param credentials refresh_token等
     * @return AuthResponse
     */
    private AuthResponse handleRefreshToken(Credentials credentials) {
        if (!JwtUtil.isVerify(credentials.getRefreshToken())) {
            throw new AppException(RCode.PARAM_ERROR.getCode(), "refresh_token无效");
        }

        Claims claims = JwtUtil.decode(credentials.getRefreshToken());
        // 用户信息
        User user = userMapperService.findByIdAndDeleteTimeIsNull(claims.get("user_id", Long.class));

        if (ObjectUtils.isEmpty(user)) {
            throw new AppException(RCode.PARAM_ERROR.getCode(), "用户已被禁");
        }
        // 生成新的access_token
        Token accessToken = new Token();
        BeanUtils.copyProperties(user, accessToken);
        accessToken.setUserId(user.getId());

        String accessTokenStr = JwtUtil.encode(LocalDateTime.now().plusSeconds(7200L), BeanUtil.beanToMap(accessToken, true, true));

        AuthResponse response = new AuthResponse();
        BeanUtils.copyProperties(user, response);
        response.setUserId(user.getId());
        response.setRefreshToken(credentials.getRefreshToken());
        response.setExpires(7200L);
        response.setAccessToken(accessTokenStr);

        RSetCache<Object> aSetCache = redissonClient.getSetCache(String.format(AppConst.ACCESS_TOKEN_KEY, user.getId()));
        aSetCache.add(accessTokenStr, 7200L, TimeUnit.SECONDS);

        return response;
    }

    /**
     * 短信验证码登录
     *
     * @param credentials 短信验证码
     * @return AuthResponse
     */
    private AuthResponse handleSmsLogin(Credentials credentials) {
        // 验证手机验证码 与 手机号
        if (StringUtils.isEmpty(credentials.getCode()) || StringUtils.isEmpty(credentials.getTenantCode())) {
            throw new AppException(RCode.PARAM_ERROR);
        }
        RBucket<String> bucket = redissonClient.getBucket(String.format(AppConst.SMS_KEY, credentials.getPhone()));
        String code = bucket.get();
        if (StringUtils.isEmpty(code) || !credentials.getCode().equals(code)) {
            throw new AppException(RCode.PARAM_ERROR.getCode(), "验证码错误");
        }
        // 验证通过删除code
        bucket.delete();

        User user = userMapper.selectOne(new QueryWrapper<User>()
                .lambda()
                .eq(User::getPhone, credentials.getPhone())
                .eq(User::getTenantCode, credentials.getTenantCode())
                .isNull(User::getDeleteTime)
        );

        return getAuthResponse(user);
    }

    /**
     * 用户名  密码  图形验证码登录
     *
     * @param credentials 用户信息
     * @return AuthResponse
     */
    private AuthResponse handleCommonLogin(Credentials credentials) {

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
                .eq(User::getPassword, DigestUtils.md5Hex(credentials.getPassword()))
                .isNull(User::getDeleteTime)
        );

        return getAuthResponse(user);
    }

    /**
     * 用户信息&token
     *
     * @param user user数据
     * @return AuthResponse
     */
    private AuthResponse getAuthResponse(User user) {
        AuthResponse response = new AuthResponse();
        if (ObjectUtils.isEmpty(user)) {
            throw new AppException(RCode.PARAM_ERROR.getCode(), "用户信息错误或已被禁");
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

        BeanUtils.copyProperties(user, response);
        response.setUserId(user.getId());

        String accessTokenStr = JwtUtil.encode(LocalDateTime.now().plusSeconds(7200L), BeanUtil.beanToMap(accessToken, true, true));
        String refreshTokenStr = JwtUtil.encode(LocalDateTime.now().plusDays(7), BeanUtil.beanToMap(refreshToken, true, true));

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
