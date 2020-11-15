package com.bootvue.auth.controller;

import com.bootvue.auth.vo.Credentials;
import com.bootvue.common.config.AppConfig;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户登录  注册  验证码  刷新token等
 */
@Api(tags = "用户认证")
@RestController
@Slf4j
public class AuthController {
    @Autowired
    private AppConfig appConfig;

    @ApiOperation("登录")
    @PostMapping("/login")
    public String login(@RequestBody Credentials credentials) {

        log.info("xxxxxxxxx: {}", appConfig.getTest());
        return null;
    }
}
