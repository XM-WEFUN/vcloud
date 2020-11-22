package com.bootvue.core.config.app;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

@RefreshScope
@Configuration
@ConfigurationProperties(prefix = "app-cloud")
@Getter
@Setter
@Slf4j
public class AppConfig {

    private Boolean swagger;  // 是否允许生成swagger文档
    private Set<String> skipUrls; //  白名单 放行uri
    private Set<Keys> authKey;   // 客户端认证凭证
    private Set<Caches> cache;  // spring cache
}
