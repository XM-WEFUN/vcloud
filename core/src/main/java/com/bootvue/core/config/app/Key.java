package com.bootvue.core.config.app;

import lombok.Data;

@Data
public class Key {
    private String appid;
    private String secret;
    private Integer platform;
    private String publicKey;
    private String privateKey;
}
