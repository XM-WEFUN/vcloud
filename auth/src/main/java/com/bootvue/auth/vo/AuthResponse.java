package com.bootvue.auth.vo;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthResponse {

    private Long userId;    // 用户id
    private String tenantCode; // 租户编号
    private String username;
    private String phone;
    private String avatar;
    private String roles;
    private String accessToken;
    private String refreshToken;
    private Long expires;  //过期时间 秒时间戳
}
