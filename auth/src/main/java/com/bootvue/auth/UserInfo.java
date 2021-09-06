package com.bootvue.auth;

import com.bootvue.core.constant.GenderEnum;
import com.bootvue.core.constant.PlatformType;
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
    private GenderEnum gender;
    private PlatformType platform;
}
