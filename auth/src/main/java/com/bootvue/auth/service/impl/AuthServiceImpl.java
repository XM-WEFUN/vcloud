package com.bootvue.auth.service.impl;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.core.io.resource.ClassPathResource;
import com.bootvue.auth.service.Oauth2ClientMapperService;
import com.bootvue.auth.dto.Captcha;
import com.bootvue.auth.dto.ClientInfo;
import com.bootvue.auth.service.AuthService;
import com.bootvue.common.constant.AppConst;
import com.bootvue.common.result.AppException;
import com.bootvue.common.result.RCode;
import com.bootvue.datasource.entity.Oauth2Client;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.awt.*;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AuthServiceImpl implements AuthService {
    private static final LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(200, 100);

    private final Oauth2ClientMapperService oauth2ClientMapperService;
    private final RedissonClient redissonClient;

    @Override
    public Captcha getCaptcha(ClientInfo clientInfo) {
        // 1 校验客户端参数
        Oauth2Client oauth2Client = validClientInfo(clientInfo);

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

    private Oauth2Client validClientInfo(ClientInfo clientInfo) {
        // 校验客户端参数 是否正确
        Oauth2Client oauth2Client = oauth2ClientMapperService.getByClientInfo(clientInfo.getTenantCode(),
                clientInfo.getClientId(), clientInfo.getSecret(), clientInfo.getPlatform());
        Assert.notNull(oauth2Client, "客户端参数错误");
        return oauth2Client;
    }
}
