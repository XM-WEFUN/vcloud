package com.bootvue.common.config.app;

import com.bootvue.common.constant.PlatformType;
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

    private String wechatAppid;  // 微信 appid
    private String wechatSecret; // 微信 app secret
    private Set<String> skipUrls; //  白名单 无需认证 放行uri
    private Set<Key> keys;   // 客户端认证凭证
    private Set<Cache> caches;  // spring cache

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
