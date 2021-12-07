package com.bootvue.datasource.type;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public enum DeptTypeEnum {  // 部门类型
    COMMON(0, "公司部门"),
    ORGAN(1, "机构/公司"),
    GROUP(2, "小组"),
    OTHER(3, "其它");

    private static final Map<Integer, DeptTypeEnum> lookup = new HashMap<>();

    static {
        EnumSet.allOf(DeptTypeEnum.class).forEach(e -> lookup.put(e.getValue(), e));
    }

    @EnumValue
    private Integer value;
    private String type;

    public static DeptTypeEnum find(Integer value) {
        return lookup.get(value);
    }
}
