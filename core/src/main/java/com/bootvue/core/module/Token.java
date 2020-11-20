package com.bootvue.core.module;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
/**
 * access token 或 refresh token
 */
@Slf4j
public class Token implements Serializable {
    private static final long serialVersionUID = -1962424963088919992L;
    private Long userId;
    private String tenantCode;
    private String username;
    private String phone;
    private String roles;
    private String type; //access_token | refresh_token
}
