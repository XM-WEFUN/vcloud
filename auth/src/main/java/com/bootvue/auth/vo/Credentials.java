package com.bootvue.auth.vo;

import com.bootvue.core.constant.AuthType;
import com.bootvue.core.constant.PlatformType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Setter
@Getter
@ToString
@ApiModel(description = "登录相关凭证")
public class Credentials {

    @ApiModelProperty(notes = "租户编号,默认000000")
    private String tenantCode;

    @ApiModelProperty(notes = "用户名")
    private String username;

    @ApiModelProperty(notes = "密码 RSA公钥加密")
    private String password;

    @ApiModelProperty(notes = "认证方式 0:refresh_token换取新token 1:用户名密码登录  2:短信验证码登录 3:小程序", required = true)
    @NotNull(message = "认证方式不能为空")
    private AuthType type;

    @ApiModelProperty(notes = "客户端平台类型 1:web 2:微信小程序 3:android 4:ios ", required = true)
    @NotNull(message = "客户端类型不能为空")
    private PlatformType platform;

    @ApiModelProperty(notes = "图形验证码或短信验证码")
    private String code;

    @ApiModelProperty(notes = "图形验证码的key")
    private String key;

    @ApiModelProperty(notes = "手机号")
    private String phone;

    @ApiModelProperty(notes = "refresh_token")
    private String refreshToken;

    @ApiModelProperty(notes = "微信小程序相关参数")
    private WechatParams wechat;
}
