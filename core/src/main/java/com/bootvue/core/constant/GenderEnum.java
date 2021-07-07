package com.bootvue.core.constant;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum GenderEnum {
    UNKNOWN(0, "未知"),
    MALE(1, "男"),
    FEMALE(2, "女");

    @EnumValue
    private final Integer value;
    private final String desc;

    private static final Map<Integer, GenderEnum> lookup = new HashMap<>();

    static {
        EnumSet.allOf(GenderEnum.class).stream().forEach(e -> lookup.put(e.getValue(), e));
    }

    public static GenderEnum find(int value) {
        return lookup.get(value);
    }
}
