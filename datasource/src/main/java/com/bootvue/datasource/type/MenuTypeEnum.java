package com.bootvue.datasource.type;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MenuTypeEnum {  // 菜单类型
    MENU(0, "菜单"),
    BUTTON(1, "按钮");

    @EnumValue
    private Integer value;
    private String type;
}
