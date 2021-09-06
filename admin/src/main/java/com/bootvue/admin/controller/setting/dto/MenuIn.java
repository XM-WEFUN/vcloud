package com.bootvue.admin.controller.setting.dto;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(description = "菜单相关参数")
public class MenuIn {
    private Long id;
    private String title;
    private Integer sort;
    private String key;
    private String path;
    private String icon;
    private Long p_id;
    private Boolean show;
    private Boolean defaultSelect;
    private Boolean defaultOpen;
}
