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
public enum PlatformType {  // 账号所属平台
    ADMIN(0, "运营平台"),
    AGENT(1, "代理平台"),
    CUSTOMER(2, "客户平台"),
    ;

    private final Integer value;
    private final String type;

    private static final Map<Integer, PlatformType> lookup = new HashMap<>();
    private static final Map<String, PlatformType> lookups = new HashMap<>();

    static {
        EnumSet.allOf(PlatformType.class).stream().forEach(e -> {
            lookup.put(e.value, e);
            lookups.put(e.type, e);
        });
    }

    public static PlatformType getPlatform(int value) {
        return lookup.get(value);
    }

    public static PlatformType getPlatform(String type) {
        return lookup.get(type);
    }
}
