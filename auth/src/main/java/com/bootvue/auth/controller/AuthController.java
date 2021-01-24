package com.bootvue.auth.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import com.bootvue.auth.service.AuthService;
import com.bootvue.auth.vo.AuthResponse;
import com.bootvue.auth.vo.CaptchaResponse;
import com.bootvue.auth.vo.Credentials;
import com.bootvue.auth.vo.PhoneParam;
import com.bootvue.core.constant.AppConst;
import com.bootvue.core.result.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.concurrent.TimeUnit;

/**
 * 用户登录  注册  验证码  刷新token等
 */
@Api(tags = "用户认证相关接口")
@RestController
@RequestMapping("/oauth")
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AuthController {

    private static final LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(200, 100);

    private final RedissonClient redissonClient;
    private final AuthService authService;

    @ApiOperation("获取token")
    @PostMapping("/token")
    public AuthResponse token(@RequestBody @Valid Credentials credentials, BindingResult result) {
        R.handleErr(result);
        return authService.authentication(credentials);
    }

    @ApiOperation("获取图形验证码")
    @GetMapping("/captcha")
    public CaptchaResponse captcha() {
        lineCaptcha.createCode();
        String code = lineCaptcha.getCode();
        String key = RandomStringUtils.randomAlphanumeric(12);
        String image = "data:image/png;base64," + lineCaptcha.getImageBase64();
        RBucket<String> bucket = redissonClient.getBucket(String.format(AppConst.CAPTCHA_KEY, key));
        bucket.set(code, 10, TimeUnit.MINUTES);

        return new CaptchaResponse(key, image);
    }

    @ApiOperation("获取短信验证码")
    @PostMapping("/sms")
    public void smsCode(@RequestBody @Valid PhoneParam phoneParam, BindingResult result) {
        R.handleErr(result);
        authService.handleSmsCode(phoneParam);
    }

    @ApiOperation("用户注册")
    @PostMapping("/register")
    public void register() {
        log.info("用户注册.....");
    }

}
