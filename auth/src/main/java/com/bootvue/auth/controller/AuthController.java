package com.bootvue.auth.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.core.util.RandomUtil;
import com.bootvue.auth.service.AuthService;
import com.bootvue.auth.vo.AuthResponse;
import com.bootvue.auth.vo.CaptchaResponse;
import com.bootvue.auth.vo.Credentials;
import com.bootvue.core.constant.AppConst;
import com.bootvue.core.result.AppException;
import com.bootvue.core.result.R;
import com.bootvue.core.result.RCode;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 用户登录  注册  验证码  刷新token等
 */
@RestController
@RequestMapping("/oauth")
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AuthController {

    private static final LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(200, 100);
    private static final Pattern phonePattern = Pattern.compile(AppConst.PHONE_REGEX);


    private final RedissonClient redissonClient;
    private final AuthService authService;

    // 用户认证
    @PostMapping("/token")
    public AuthResponse token(@RequestBody @Valid Credentials credentials, BindingResult result) {
        R.handleErr(result);
        return authService.authentication(credentials);
    }

    // 图形验证码
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

    // 短信验证码
    @GetMapping("/sms")
    public void smsCode(@RequestParam("phone") String phone) {
        Matcher matcher = phonePattern.matcher(phone);
        if (!matcher.find()) {
            throw new AppException(RCode.PARAM_ERROR.getCode(), "手机号错误");
        }
        String code = RandomUtil.randomNumbers(6);
        RBucket<String> bucket = redissonClient.getBucket(String.format(AppConst.SMS_KEY, phone));
        bucket.set(code, 15L, TimeUnit.MINUTES);
        log.info("短信验证码 : {}", code);
    }

}
