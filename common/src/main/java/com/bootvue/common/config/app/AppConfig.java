package com.bootvue.common.config.app;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import java.util.Set;

@RefreshScope
@Configuration
@ConfigurationProperties(prefix = "vcloud")
@Getter
@Setter
@Slf4j
public class AppConfig {

    private Set<String> skipUrls; //  白名单 无需认证 放行uri

    private Set<Cache> caches; // redis cache

    private String publicKey; // rsa 公钥

    private String privateKey; // rsa 私钥

}
