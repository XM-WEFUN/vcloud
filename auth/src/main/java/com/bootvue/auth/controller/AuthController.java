package com.bootvue.api.controller;

import cn.hutool.core.util.RandomUtil;
import com.bootvue.api.vo.Credentials;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户登录  注册  验证码  刷新token等
 */
@Api(tags = "用户认证")
@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    @ApiOperation("登录")
    @PostMapping("/login")
    public String login(@RequestBody Credentials credentials) {
        return RandomUtil.randomNumbers(5);
    }
}
