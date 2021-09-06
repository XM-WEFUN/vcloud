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
    CUSTOMER(1, "客户平台"),
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
