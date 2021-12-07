package com.bootvue.admin.service;

import com.bootvue.common.annotation.PreAuth;
import com.bootvue.common.serializer.LongToStringSerializer;
import com.bootvue.datasource.model.Base;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("oauth2 client信息")
@PreAuth(superOnly = true, hasRole = "admin")
public class Oauth2Item extends Base {


    private String clientId;

    private String secret;

    @ApiModelProperty("认证类型 code,password,refresh_token")
    private String grantType;

    @ApiModelProperty("scope all||basic_info")
    private String scope;

    @ApiModelProperty("客户端平台类型 0 WEB 1 APP 2 小程序")
    private Integer platform;

    @JsonSerialize(using = LongToStringSerializer.class)
    @ApiModelProperty("access token有效时间 s")
    private Long accessTokenExpire;

    @JsonSerialize(using = LongToStringSerializer.class)
    @ApiModelProperty("refresh token有效时间 s")
    private Long refreshTokenExpire;

    @ApiModelProperty("认证重定向url")
    private String redirectUrl;

}
