package com.bootvue.datasource.type;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DeptTypeEnum {  // 部门类型
    COMMON(0, "公司部门"),
    ORGAN(1, "机构/公司"),
    GROUP(2, "小组"),
    OTHER(3, "其它");

    @EnumValue
    private Integer value;
    private String type;
}
