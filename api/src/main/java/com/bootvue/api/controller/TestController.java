package com.bootvue.api.controller;

import cn.hutool.core.util.RandomUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(tags = "测试controller")
public class TestController {

    @ApiOperation(("测试方法"))
    @GetMapping("/test")
    public String test() {
        return RandomUtil.randomString(4);
    }
}
