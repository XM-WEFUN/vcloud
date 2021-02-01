package com.bootvue.core.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public enum PlatformType {
    WEB(0, "web"),
    WECHAT(1, "wechat"),
    ANDROID(2, "android"),
    IOS(3, "ios"),
    ;

    private Integer value;
    private String type;
}
