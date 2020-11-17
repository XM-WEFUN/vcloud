package com.bootvue.api.controller;

import cn.hutool.core.util.RandomUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
@Api(tags = "测试controller")
public class TestController {

    @ApiOperation(("测试方法"))
    @GetMapping("/test")
    public String test() {
        return RandomUtil.randomString(4);
    }

    @PostMapping("/upload")
    public void upload(@RequestParam MultipartFile file) {
        log.info("文件上传: {}  {}", file.getContentType(), file.getOriginalFilename());
    }
}
