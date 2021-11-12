package com.bootvue.common.model;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AppUser {
    private Long id; // 用户id
    private Long tenantId; // 租户id
    private String username;
    private String openid;
    private String label;  // TokenLabelEnum
}
