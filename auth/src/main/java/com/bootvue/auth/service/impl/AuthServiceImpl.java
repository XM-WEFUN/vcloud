package com.bootvue.auth.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bootvue.auth.service.AuthService;
import com.bootvue.auth.vo.AuthResponse;
import com.bootvue.auth.vo.Credentials;
import com.bootvue.auth.vo.PhoneParams;
import com.bootvue.auth.vo.WechatParams;
import com.bootvue.core.config.app.AppConfig;
import com.bootvue.core.config.app.Keys;
import com.bootvue.core.constant.AppConst;
import com.bootvue.core.entity.User;
import com.bootvue.core.mapper.UserMapper;
import com.bootvue.core.module.Token;
import com.bootvue.core.result.AppException;
import com.bootvue.core.result.RCode;
import com.bootvue.core.service.UserMapperService;
import com.bootvue.core.util.JwtUtil;
import com.bootvue.core.util.RsaUtil;
import com.bootvue.core.wechat.WechatApi;
import com.bootvue.core.wechat.WechatUtil;
import com.bootvue.core.wechat.vo.WechatSession;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AuthServiceImpl implements AuthService {
    private final RedissonClient redissonClient;
    private final UserMapper userMapper;
    private final UserMapperService userMapperService;
    private final AppConfig appConfig;

    @Override
    public AuthResponse authentication(Credentials credentials) {

        switch (credentials.getType()) {
            case USERNAME_PASSWORD_LOGIN:
                return handleCommonLogin(credentials);
            case SMS_LOGIN:
                return handleSmsLogin(credentials);
            case REFRESH_TOKEN:
                return handleRefreshToken(credentials);
            case WECHAT:
                return handleWechatLogin(credentials);
            default:
                throw new AppException(RCode.PARAM_ERROR.getCode(), "认证方式错误");
        }
    }

    /**
     * 微信小程序认证
     *
     * @param credentials 小程序相关参数
     * @return AuthResponse
     */
    private AuthResponse handleWechatLogin(Credentials credentials) {
        try {
            // 1. 获取openid与session_key
            Keys keys = AppConfig.getKeys(appConfig, credentials.getPlatform());
            WechatParams wechat = credentials.getWechat();

            WechatSession wechatSession = WechatApi.code2Session(wechat.getCode(), keys.getWechatAppid(), keys.getWechatSecret());
            if (ObjectUtils.isEmpty(wechatSession) || !StringUtils.hasText(wechatSession.getOpenid()) || !StringUtils.hasText(wechatSession.getSessionKey())) {
                throw new AppException(RCode.PARAM_ERROR);
            }

            // 2. 校验数据签名
            if (!DigestUtils.sha1Hex(wechat.getRawData() + wechatSession.getSessionKey()).equalsIgnoreCase(wechat.getSignature())) {
                log.error("微信小程序加密参数校验失败");
                throw new AppException(RCode.PARAM_ERROR);
            }

            // 3. 敏感数据暂不处理
            log.info("加密数据: {}", WechatUtil.decrypt(wechatSession.getSessionKey(), wechat.getEncryptedData(), wechat.getIv()));

            // 4. 用户是否存在  新增/更新用户
            User user = userMapperService.findByOpenid(wechatSession.getOpenid(), credentials.getTenantCode());

            if (ObjectUtils.isEmpty(user)) {
                user = new User(null, credentials.getTenantCode(), "wx_" + RandomUtil.randomString(5) + wechat.getNickName().substring(0, 2).trim(),
                        "", wechatSession.getOpenid(), RandomUtil.randomString(32), wechat.getNickName(),
                        wechat.getAvatarUrl(), "", true, LocalDateTime.now(), null, null);
                userMapper.insert(user);
            } else {
                user.setAvatar(wechat.getAvatarUrl());
                user.setNickname(wechat.getNickName());
                user.setUpdateTime(LocalDateTime.now());
                userMapper.updateById(user);
            }

            return getAuthResponse(user);

        } catch (Exception e) {
            log.error("微信小程序用户认证失败: 参数: {}", credentials);
            throw new AppException(RCode.PARAM_ERROR);
        }

    }

    @Override
    public void handleSmsCode(PhoneParams phoneParams) {
        // 校验手机号是否存在
        User user = userMapperService.findByPhone(phoneParams.getPhone(), phoneParams.getTenantCode());
        if (ObjectUtils.isEmpty(user)) {
            throw new AppException(RCode.PARAM_ERROR);
        }
        String code = RandomUtil.randomNumbers(6);
        RBucket<String> bucket = redissonClient.getBucket(String.format(AppConst.SMS_KEY, phoneParams.getPhone()));
        bucket.set(code, 15L, TimeUnit.MINUTES);
        log.info("短信验证码 : {}", code);
    }

    /**
     * 换取新的access_token
     * refresh_token也一并更新
     *
     * @param credentials refresh_token等
     * @return AuthResponse
     */
    private AuthResponse handleRefreshToken(Credentials credentials) {
        if (!JwtUtil.isVerify(credentials.getRefreshToken())) {
            throw new AppException(RCode.PARAM_ERROR.getCode(), "refresh_token无效");
        }

        Claims claims = JwtUtil.decode(credentials.getRefreshToken());
        String type = claims.get("type", String.class);
        if (!StringUtils.hasText(type) || !AppConst.REFRESH_TOKEN.equalsIgnoreCase(type)) {
            throw new AppException(RCode.PARAM_ERROR.getCode(), "refresh_token无效");
        }

        // 用户信息
        User user = userMapperService.findById(claims.get("user_id", Long.class));

        return getAuthResponse(user);
    }

    /**
     * 短信验证码登录
     *
     * @param credentials 短信验证码
     * @return AuthResponse
     */
    private AuthResponse handleSmsLogin(Credentials credentials) {
        // 验证手机验证码 与 手机号
        if (!StringUtils.hasText(credentials.getCode()) || !StringUtils.hasText(credentials.getTenantCode())) {
            throw new AppException(RCode.PARAM_ERROR);
        }
        RBucket<String> bucket = redissonClient.getBucket(String.format(AppConst.SMS_KEY, credentials.getPhone()));
        String code = bucket.get();
        if (!StringUtils.hasText(code) || !credentials.getCode().equals(code)) {
            throw new AppException(RCode.PARAM_ERROR.getCode(), "验证码错误");
        }
        // 验证通过删除code
        bucket.delete();

        return getAuthResponse(userMapperService.findByPhone(credentials.getPhone(), credentials.getTenantCode()));
    }

    /**
     * 用户名  密码  图形验证码登录
     *
     * @param credentials 用户信息
     * @return AuthResponse
     */
    private AuthResponse handleCommonLogin(Credentials credentials) {

        if (!StringUtils.hasText(credentials.getKey()) || !StringUtils.hasText(credentials.getUsername()) ||
                !StringUtils.hasText(credentials.getPassword()) || !StringUtils.hasText(credentials.getCode())) {
            throw new AppException(RCode.PARAM_ERROR);
        }

        // 校验验证码
        RBucket<String> bucket = redissonClient.getBucket(String.format(AppConst.CAPTCHA_KEY, credentials.getKey()));
        String storedCode = bucket.getAndDelete();
        if (!StringUtils.hasText(storedCode) || !credentials.getCode().equalsIgnoreCase(storedCode)) {
            throw new AppException(RCode.PARAM_ERROR.getCode(), "验证码无效");
        }

        String password = null;
        try {
            Keys keys = AppConfig.getKeys(appConfig, credentials.getPlatform());
            assert keys != null;
            password = DigestUtils.md5Hex(RsaUtil.decrypt(keys.getPrivateKey(), credentials.getPassword()));
        } catch (Exception e) {
            throw new AppException(RCode.PARAM_ERROR);
        }

        // 验证 用户名 密码
        User user = userMapper.selectOne(new QueryWrapper<User>()
                .lambda()
                .eq(User::getUsername, credentials.getUsername()).eq(User::getTenantCode, credentials.getTenantCode())
                .eq(User::getPassword, password)
                .isNull(User::getDeleteTime)
        );

        return getAuthResponse(user);
    }

    /**
     * 用户信息&token
     *
     * @param user user对象
     * @return AuthResponse
     */
    private AuthResponse getAuthResponse(User user) {
        AuthResponse response = new AuthResponse();
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

        BeanUtils.copyProperties(user, response);
        response.setUserId(user.getId());

        //  access_token 7200s
        LocalDateTime accessTokenExpire = LocalDateTime.now().plusSeconds(7200L);
        // refresh_token 20d
        LocalDateTime refreshTokenExpire = LocalDateTime.now().plusDays(20L);

        String accessTokenStr = JwtUtil.encode(accessTokenExpire, BeanUtil.beanToMap(accessToken, true, true));
        String refreshTokenStr = JwtUtil.encode(refreshTokenExpire, BeanUtil.beanToMap(refreshToken, true, true));

        response.setAccessToken(accessTokenStr);
        response.setRefreshToken(refreshTokenStr);
        response.setExpires(accessTokenExpire.atZone(ZoneId.of("+8")).toEpochSecond());

        return response;
    }

}
