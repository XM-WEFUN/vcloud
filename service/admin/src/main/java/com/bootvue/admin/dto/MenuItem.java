package com.bootvue.admin.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("菜单")
public class MenuItem implements Serializable {

    private static final long serialVersionUID = 2752549007939109954L;

    @ApiModelProperty("菜单/按钮名称")
    private String title;
    @ApiModelProperty("唯一标识")
    private String key;
    @ApiModelProperty("前端路由")
    private String path;
    @ApiModelProperty("icon")
    private String icon;
    @ApiModelProperty("权限字段 例: list,add,update,delete")
    private String action;
    @ApiModelProperty("菜单栏是否展示")
    private Boolean show;
    @ApiModelProperty("是否默认选中")
    private Boolean defaultSelect;
    @ApiModelProperty("是否默认展开")
    private Boolean defaultOpen;
    @ApiModelProperty("子菜单")
    private List<MenuItem> children;
}
