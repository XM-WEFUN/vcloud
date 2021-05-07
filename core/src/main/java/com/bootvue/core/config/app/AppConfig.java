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
    private Set<String> skipUrls; //  白名单 放行uri
    private Set<String> authorizationUrls; // 需要权限验证的接口
    private Set<Keys> authKey;   // 客户端认证凭证
    private Set<Caches> cache;  // spring cache

    /**
     * 获取客户端对应的密钥等参数
     *
     * @param appConfig    appConfig
     * @param platformType 客户端类型
     * @return Keys
     */
    public static Keys getKeys(AppConfig appConfig, PlatformType platformType) {

        for (Keys keys : appConfig.getAuthKey()) {
            if (keys.getPlatform().equals(platformType.getValue())) {
                return keys;
            }
        }
        return null;
    }
}
