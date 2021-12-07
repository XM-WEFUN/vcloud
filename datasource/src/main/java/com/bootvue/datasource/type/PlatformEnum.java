package com.bootvue.datasource.type;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum PlatformEnum {  // 客户端平台类型
    WEB(0, "web"),
    APP(1, "app"),
    WECHAT_APP(2, "小程序");

    private static final Map<Integer, PlatformEnum> lookup = new HashMap<>();

    static {
        EnumSet.allOf(PlatformEnum.class).forEach(e -> lookup.put(e.getValue(), e));
    }

    @EnumValue
    private Integer value;
    private String type;

    public static PlatformEnum find(Integer value) {
        return lookup.get(value);
    }
}
