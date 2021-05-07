package com.bootvue.core.module;

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
    private Long userId;
    private String username;
    private Long tenantId;
    private String type;  // token类型   access_token | refresh_token
    private Long roleId; //角色id
}
