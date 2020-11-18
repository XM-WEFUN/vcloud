package com.bootvue.api.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

@RestController
@Slf4j
@Api(tags = "测试controller")
public class TestController {

    @ApiOperation(("测试方法"))
    @PostMapping("/test")
    public String test(@RequestBody Demo demo, HttpServletRequest request) {
        log.info("{}  {}", request.getHeader("xxxx"), demo);
        return "<br/>";
    }

    @PostMapping("/upload")
    public void upload(@RequestParam MultipartFile file, HttpServletRequest request) {
        log.info("文件上传: {}  {} {}", file.getContentType(), file.getOriginalFilename(), request.getHeader("xxxx"));
    }
}
