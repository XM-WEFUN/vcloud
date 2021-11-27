package com.bootvue.datasource.type;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GrantTypeEnum {  // 认证类型 code||password||refresh_token
    CODE(0, "code"),
    PASSWORD(1, "password"),
    REFRESH_TOKEN(2, "refresh_token");

    @EnumValue
    private Integer value;
    private String type;
}
