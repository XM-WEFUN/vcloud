package com.bootvue.datasource.type;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AccountTypeEnum {    // 账号类型
    COMMON(0, "普通用户"),
    TENANT_ADMIN(1, "租户账号"),
    ADMIN(2, "平台账号");

    @EnumValue
    private Integer value;
    private String type;
}
