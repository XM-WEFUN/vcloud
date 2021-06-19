package com.bootvue.core.config.app;

import com.bootvue.core.constant.PlatformType;
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

    private Boolean swagger;  // 是否允许生成swagger文档
    private Set<String> skipUrls; //  白名单 无需认证 放行uri
    private Set<String> authorizationUrls; // 需要权限验证的接口
    private Set<String> unAuthorizationUrls; // 不需要权限校验
    private Set<Key> keys;   // 客户端认证凭证
    private Set<Caches> cache;  // spring cache

    /**
     * 获取客户端对应的密钥等参数
     *
     * @param appConfig    appConfig
     * @param platformType 客户端类型
     * @return Key
     */
    public static Key getKeys(AppConfig appConfig, PlatformType platformType) {

        for (Key keys : appConfig.getKeys()) {
            if (keys.getPlatform().equals(platformType.getValue())) {
                return keys;
            }
        }
        return null;
    }
}
