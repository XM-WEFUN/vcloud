package com.bootvue.auth.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ApiModel(description = "用户信息&token")
public class AuthResponse {

    @ApiModelProperty("user id")
    private Long userId;
    @ApiModelProperty("租户编号")
    private String tenantCode;
    @ApiModelProperty("用户名")
    private String username;
    @ApiModelProperty("昵称")
    private String nickname;
    @ApiModelProperty("手机号")
    private String phone;
    @ApiModelProperty("头像")
    private String avatar;
    @ApiModelProperty("roles")
    private String roles;
    @ApiModelProperty("access_token")
    private String accessToken;
    @ApiModelProperty("refresh_token 有效时间20d")
    private String refreshToken;
    @ApiModelProperty("access_token过期时间 7200s")
    private Long expires;
}
