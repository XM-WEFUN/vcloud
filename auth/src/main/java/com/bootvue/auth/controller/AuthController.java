package com.bootvue.auth.controller;

import com.bootvue.auth.vo.Credentials;
import com.bootvue.common.config.AppConfig;
import com.bootvue.common.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户登录  注册  验证码  刷新token等
 */
@Api(tags = "用户认证")
@RestController
@RequestMapping("/oauth")
@Slf4j
public class AuthController {
    @Autowired
    private AppConfig appConfig;

    @ApiOperation("登录获取token")
    @PostMapping("/token")
    public String login(@RequestBody Credentials credentials) {
        Map<String, Object> mapper = new HashMap<>();
        mapper.put("xx", "嘿嘿");
        String encode = JwtUtil.encode(300000L, mapper);
        log.info("{}", encode);

        Claims decode = JwtUtil.decode(encode);
        log.info("{}", decode);
        return "xxx";
    }

}
