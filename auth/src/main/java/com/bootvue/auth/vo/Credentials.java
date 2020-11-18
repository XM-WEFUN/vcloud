package com.bootvue.auth.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Setter
@Getter
@ApiModel(description = "用户认证凭证")
public class Credentials {

    @ApiModelProperty(notes = "用户名")
    private String username;

    @ApiModelProperty(notes = "密码")
    private String password;

    @ApiModelProperty(notes = "类型 0:普通用户名密码  1:短信登录  2: refresh_token获取新token")
    @NotEmpty(message = "认证类型不能为空")
    private Integer type;

    @ApiModelProperty(notes = "图形验证码或短信验证码")
    private String code;

    @ApiModelProperty(notes = "手机号")
    private String phone;

    @ApiModelProperty(notes = "refresh_token")
    private String refreshToken;

    @ApiModelProperty(notes = "appId")
    @NotEmpty(message = "appid不能为空")
    private String appid;

    @ApiModelProperty(notes = "secret")
    @NotEmpty(message = "secret不能为空")
    private String secret;

    @ApiModelProperty(notes = "scope, 固定: all")
    @NotEmpty(message = "scope不能为空")
    private String scope;
}
