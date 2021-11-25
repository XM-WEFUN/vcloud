package com.bootvue.datasource.type;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AccountTypeEnum {    // 账号类型
    COMMON(0, "普通用户"),
    TENANT_ADMIN(1, "租户管理员"),
    ADMIN(2, "普通管理员");

    @EnumValue
    private Integer value;
    private String type;
}
