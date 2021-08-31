package com.bootvue.auth.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.util.List;

@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("菜单权限")
public class MenuOut {
    @ApiModelProperty(notes = "菜单key", required = true)
    private String key;

    @ApiModelProperty(notes = "菜单icon")
    private String icon;

    @ApiModelProperty(notes = "菜单名称")
    private String title;

    @ApiModelProperty(notes = "路由path")
    private String path;

    @ApiModelProperty(notes = "是否展示在菜单栏")
    private Boolean show;

    @ApiModelProperty(notes = "菜单栏是否处于默认选择的状态")
    private Boolean defaultSelect;

    @ApiModelProperty(notes = "二级菜单是否默认展开")
    private Boolean defaultOpen;

    @ApiModelProperty(notes = "子菜单")
    private List<MenuOut> children;
}
