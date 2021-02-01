package com.bootvue.core.config.app;

import lombok.Data;

@Data
public class Keys {
    private String appid;
    private String secret;
    private Integer platform;
    private String publicKey;
    private String privateKey;
    private String wechatAppid;
    private String wechatSecret;
}
