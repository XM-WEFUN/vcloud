package com.bootvue.auth.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@ApiModel("token认证信息")
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {

    @ApiModelProperty(notes = "用户名", required = true)
    private String username;

    @ApiModelProperty(notes = "access token 有效时间7200s", required = true)
    private String accessToken;

    @ApiModelProperty(notes = "refresh token", required = true)
    private String refreshToken;

    @ApiModelProperty(notes = "refresh_token 有效时长秒", required = true)
    private Long expire;

    @ApiModelProperty(notes = "手机号")
    private String phone;

    @ApiModelProperty(notes = "头像")
    private String avatar;

    @ApiModelProperty(notes = "性别 0未知 1男 2女")
    private Integer gender;

    @ApiModelProperty(notes = "菜单权限")
    private List<MenuOut> menus;
}
