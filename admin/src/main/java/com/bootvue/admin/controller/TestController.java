package com.bootvue.admin.controller;

import com.bootvue.core.constant.Roles;
import com.bootvue.web.annotation.PreAuth;
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
@PreAuth(Roles.ADMIN)
public class TestController {

    @PostMapping("/test")
    public Demo test(@RequestBody Demo demo, HttpServletRequest request) {
        log.info("{}  {}", request.getHeader("username"), demo);
        return new Demo(null, LocalDateTime.now());
    }

    @PostMapping("/upload")
    public void upload(@RequestParam MultipartFile file, HttpServletRequest request) {
        log.info("文件上传: {}  {} {}", file.getContentType(), file.getOriginalFilename(), request.getHeader("username"));
    }
}
