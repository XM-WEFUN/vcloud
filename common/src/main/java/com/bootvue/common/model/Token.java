package com.bootvue.common.model;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
/*
  access token 或 refresh token
*/
@Slf4j
public class Token implements Serializable {
    private static final long serialVersionUID = -1962424963088919992L;

    private Long id;
    private Long tenantId;
    private String username;
    private String type;  // token类型   access_token | refresh_token
    private Integer accountType; // 账号类型   0普通用户  1租户管理员  2平台管理员
}
