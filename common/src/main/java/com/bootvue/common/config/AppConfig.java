package com.bootvue.common.config;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

@RefreshScope
@Configuration
@ConfigurationProperties(prefix = "app-cloud")
@Getter
@Setter
@Slf4j
public class AppConfig {

    private Boolean swagger;  // 是否允许生成swagger文档
    private String test;

}
