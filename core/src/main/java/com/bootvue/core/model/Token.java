package com.bootvue.core.model;

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
    private String username;
    private Long tenantId;
    private Integer platform; // 账号所属平台
    private String type;  // token类型   access_token | refresh_token
}
