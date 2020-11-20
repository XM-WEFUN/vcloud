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

    private String code;

    private String key;

    private String phone;

    private String refreshToken;

}
