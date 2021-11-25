package com.bootvue.datasource.type;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PlatformEnum {  // 客户端平台类型
    WEB(0, "web"),
    APP(1, "app"),
    WECHAT_APP(2, "小程序");

    @EnumValue
    private Integer value;
    private String type;
}
