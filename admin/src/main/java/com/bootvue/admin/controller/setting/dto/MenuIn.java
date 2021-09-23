package com.bootvue.admin.controller.setting.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(description = "菜单相关参数")
public class MenuIn {

    @ApiModelProperty(notes = "菜单id")
    private Long id;
    @ApiModelProperty(notes = "菜单标题")
    private String title;
    @ApiModelProperty(notes = "菜单顺序")
    private Integer sort;
    @ApiModelProperty(notes = "唯一标识key")
    private String key;
    @ApiModelProperty(notes = "前端路由路径")
    private String path;
    @ApiModelProperty(notes = "and design vue icon图标")
    private String icon;
    @ApiModelProperty(notes = "上一级id")
    private Long p_id;
    @ApiModelProperty(notes = "是否展示在菜单栏")
    private Boolean show;
    @ApiModelProperty(notes = "是否默认选中")
    private Boolean defaultSelect;
    @ApiModelProperty(notes = "二级菜单是否默认展开")
    private Boolean defaultOpen;
}
