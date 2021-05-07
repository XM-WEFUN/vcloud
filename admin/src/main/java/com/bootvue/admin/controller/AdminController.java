package com.bootvue.admin.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@RestController
@Slf4j
@Api(tags = "admin相关接口")
public class AdminController {

    @PostMapping("/test")
    @ApiOperation("test测试接口")
    public Demo test(@RequestBody Demo demo, HttpServletRequest request) {
        log.info("{}  {}", request.getHeader("username"), demo);

        return new Demo(null, LocalDateTime.now());
    }

    @ApiOperation("upload测试")
    @PostMapping("/upload")
    public void upload(@RequestParam MultipartFile file, HttpServletRequest request) {
        log.info("文件上传: {}  {} {}", file.getContentType(), file.getOriginalFilename(), request.getHeader("username"));
    }
}
