package com.bootvue.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {
    private Long id;  // 用户id
    private String username;
    private String phone;
    private String avatar;
    private Long tenantId;
    private Integer platform;
    private Long roleId;  // 角色id  >0 管理用户  =0 未分配角色的管理账号  -1普通用户
}
