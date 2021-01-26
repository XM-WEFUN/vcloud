package com.bootvue.auth.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Setter
@Getter
@ApiModel(description = "登录相关凭证")
public class Credentials {

    @ApiModelProperty(notes = "租户编号,默认000000")
    private String tenantCode;

    @ApiModelProperty(notes = "用户名")
    private String username;

    @ApiModelProperty(notes = "密码")
    private String password;

    @ApiModelProperty(notes = "认证类型  0:用户名密码登录  1:短信验证码登录  2:refresh_token换取新token", required = true)
    @NotNull(message = "认证类型不能为空")
    private Integer type;

    @ApiModelProperty(notes = "图形验证码或短信验证码")
    private String code;

    @ApiModelProperty(notes = "图形验证码的key")
    private String key;

    @ApiModelProperty(notes = "手机号")
    private String phone;

    @ApiModelProperty(notes = "refresh_token")
    private String refreshToken;

}
