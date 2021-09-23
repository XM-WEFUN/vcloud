package com.bootvue.auth.dto;

import com.bootvue.core.constant.TokenLabelEnum;
import com.bootvue.db.type.GenderEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {
    private Long id;  // 用户id
    private Long tenantId;
    private String username;
    private String phone;
    private String avatar;
    private GenderEnum gender;
    private TokenLabelEnum label;
    private List<Long> roleIds;
}
