package com.bootvue.auth.vo;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Setter
@Getter
public class Credentials {

    @NotEmpty(message = "租户编号不能为空")
    private String tenantCode;

    private String username;

    private String password;

    @NotNull(message = "认证类型不能为空")
    private Integer type;

    private String code; // 图形验证码或短信验证码

    private String key;  // 图形验证码的key

    private String phone;  // 手机号

    private String refreshToken;

}
