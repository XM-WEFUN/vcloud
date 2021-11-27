package com.bootvue.auth.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
@ApiModel("用户认证响应")
public class AuthResponse {

    @ApiModelProperty("用户账号")
    private String account;
    @ApiModelProperty("昵称")
    private String nickName;
    @ApiModelProperty("性别")
    private String gender;
    @ApiModelProperty("头像")
    private String avatar;
    @ApiModelProperty("access token")
    private String accessToken;
    @ApiModelProperty("refresh token")
    private String refreshToken;
}
