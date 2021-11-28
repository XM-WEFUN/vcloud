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

    @ApiModelProperty("access token")
    private String accessToken;
    @ApiModelProperty("refresh token")
    private String refreshToken;
}
