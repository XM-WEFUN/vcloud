package com.bootvue.auth.service.impl;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import com.bootvue.auth.dto.AuthParam;
import com.bootvue.auth.dto.AuthResponse;
import com.bootvue.auth.dto.Captcha;
import com.bootvue.auth.dto.ClientInfo;
import com.bootvue.auth.service.AuthService;
import com.bootvue.auth.service.Oauth2ClientMapperService;
import com.bootvue.auth.service.UserMapperService;
import com.bootvue.common.constant.AppConst;
import com.bootvue.common.model.Token;
import com.bootvue.common.result.AppException;
import com.bootvue.common.result.RCode;
import com.bootvue.common.util.JwtUtil;
import com.bootvue.common.util.RsaUtil;
import com.bootvue.datasource.entity.Oauth2Client;
import com.bootvue.datasource.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AuthServiceImpl implements AuthService {
    private static final LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(200, 100);

    private final RedissonClient redissonClient;
    private final Oauth2ClientMapperService oauth2ClientMapperService;
    private final UserMapperService userMapperService;

    @Override
    public Captcha getCaptcha(ClientInfo clientInfo) {
        // 1 校验客户端参数
        validClientInfo(clientInfo);

        // 2 生成验证码
        String key = RandomStringUtils.randomAlphanumeric(12);

        Font font = null;
        try {
            font = Font.createFont(Font.TRUETYPE_FONT, new ClassPathResource("font.ttf").getStream())
                    .deriveFont(Font.PLAIN, 75.0f);
        } catch (Exception e) {
            log.error("字体加载失败.....", e);
            throw new AppException(RCode.DEFAULT);
        }
        lineCaptcha.setFont(font);
        lineCaptcha.createCode();
        String code = lineCaptcha.getCode();
        String image = lineCaptcha.getImageBase64Data();
        RBucket<String> bucket = redissonClient.getBucket(String.format(AppConst.CAPTCHA_KEY, key));
        bucket.set(code, 10, TimeUnit.MINUTES);

        return new Captcha(key, image);
    }

    @Override
    public AuthResponse token(AuthParam param) {

        Oauth2Client clientInfo = validClientInfo(new ClientInfo(param.getTenantCode(), param.getClientId(), param.getSecret(), param.getPlatform()));

        switch (param.getGrantType()) {
            case CODE:
                log.info("用户认证: code 模式.......");
                return null;
            case PASSWORD:
                log.info("用户认证: password 模式.......");
                return handlePasswordAuth(param, clientInfo);
            case REFRESH_TOKEN:
                log.info("用户认证: refresh_token 模式.......");
                return null;
            default:
                throw new AppException(RCode.PARAM_ERROR);
        }
    }

    /**
     * password模式认证
     *
     * @param param
     * @param clientInfo
     * @return
     */
    private AuthResponse handlePasswordAuth(AuthParam param, Oauth2Client clientInfo) {
        // 1 验证图形验证码
        if (!StringUtils.hasText(param.getKey()) || !StringUtils.hasText(param.getCaptcha())
                || !StringUtils.hasText(param.getAccount()) || !StringUtils.hasText(param.getPassword())) {
            throw new AppException(RCode.PARAM_ERROR);
        }

        RBucket<String> bucket = redissonClient.getBucket(String.format(AppConst.CAPTCHA_KEY, param.getKey()));
        String captchaCode = bucket.getAndDelete();
        Assert.isTrue(param.getCaptcha().equalsIgnoreCase(captchaCode), "验证码错误");

        // 2 校验账号 密码
        String password = RsaUtil.decrypt(clientInfo.getPrivateKey(), param.getPassword());
        User user = userMapperService.getByTenantCodeAndAccount(param.getTenantCode(), param.getAccount(), DigestUtils.md5Hex(password));

        return genResponse(user, clientInfo);
    }

    private AuthResponse genResponse(User user, Oauth2Client clientInfo) {
        // 校验用户信息
        Assert.notNull(user, "凭证错误");
        Assert.isTrue(user.getStatus(), "用户已被禁用");
        Assert.isNull(user.getDeleteTime(), "用户已被删除");

        // 生成token
        AuthResponse response = new AuthResponse();

        response.setAccount(user.getAccount());
        response.setNickName(user.getNickName());
        response.setGender(user.getGender().getDesc());
        response.setAvatar(user.getAvatar());

        Token accessToken = new Token(user.getId(), user.getTenantId(), user.getAccount(), AppConst.ACCESS_TOKEN, user.getType().getValue());
        Token refreshToken = new Token(user.getId(), user.getTenantId(), user.getAccount(), AppConst.REFRESH_TOKEN, user.getType().getValue());

        response.setAccessToken(JwtUtil.encode(LocalDateTime.now().plusSeconds(clientInfo.getAccessTokenExpire()), BeanUtil.beanToMap(accessToken)));
        response.setRefreshToken(JwtUtil.encode(LocalDateTime.now().plusSeconds(clientInfo.getRefreshTokenExpire()), BeanUtil.beanToMap(refreshToken)));

        return response;
    }

    private Oauth2Client validClientInfo(ClientInfo clientInfo) {
        // 校验客户端参数 是否正确
        Oauth2Client oauth2Client = oauth2ClientMapperService.getByClientInfo(clientInfo.getTenant_code(),
                clientInfo.getClient_id(), clientInfo.getSecret(), clientInfo.getPlatform().getValue());
        Assert.notNull(oauth2Client, "客户端参数错误");
        return oauth2Client;
    }
}
