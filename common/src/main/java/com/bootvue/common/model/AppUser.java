package com.bootvue.common.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AppUser {
    private Long id;  // 用户id
    private Long tenantId; // 租户id
    private String account; // 用户账号
    private Integer type; // 账号类型
}
