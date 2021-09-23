package com.bootvue.core.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TokenLabelEnum {  // token 用户类型  管理类用户 | 普通用户
    ADMIN("ADMIN"), USER("USER");

    String value;
}
