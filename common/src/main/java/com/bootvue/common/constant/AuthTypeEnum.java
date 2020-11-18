package com.bootvue.common.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AuthTypeEnum {
    COMMON(0, "用户名密码登录"),
    SMS(1, "短信验证码登录"),
    REFRESH(2, "refresh_token换取新的access_token");
    private final Integer type;
    private final String desc;
}
