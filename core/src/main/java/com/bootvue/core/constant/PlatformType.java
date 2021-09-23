package com.bootvue.core.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@Getter
@ToString
public enum PlatformType {  // 客户端类型
    WEB(0, "web端"),
    WECHAT_APP(1, "微信小程序端"),
    ;

    private static final Map<Integer, PlatformType> lookup = new HashMap<>();
    private static final Map<String, PlatformType> lookups = new HashMap<>();

    static {
        EnumSet.allOf(PlatformType.class).forEach(e -> {
            lookup.put(e.value, e);
            lookups.put(e.type, e);
        });
    }

    private final Integer value;
    private final String type;

    public static PlatformType getPlatform(int value) {
        return lookup.get(value);
    }

    public static PlatformType getPlatform(String type) {
        return lookup.get(type);
    }
}
