package com.bootvue.auth.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotEmpty(message = "用户名不能为空")
    private String username;
    @NotEmpty(message = "密码不能为空")
    private String password;
    @NotEmpty(message = "验证码不能为空")
    private String code;  // 验证码
    @NotEmpty(message = "租户编号不能为空")
    private String tenantCode;  // 租户编号
}
