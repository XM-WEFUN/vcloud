package com.bootvue.admin.controller.setting.dto;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "一级菜单信息")
public class MenuParentList {
    private Long id;
    private String title;
}
