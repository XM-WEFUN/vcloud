package com.bootvue.admin.controller.setting.dto;

import com.bootvue.core.serializer.LongToStringSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "菜单列表信息")
public class MenuListOut {

    @JsonSerialize(using = LongToStringSerializer.class)
    private Long id;
    @ApiModelProperty(notes = "标题")
    private String title;
    @ApiModelProperty(notes = "顺序")
    private Integer sort;
    @ApiModelProperty(notes = "菜单唯一标识")
    private String key;
    @ApiModelProperty(notes = "router路径")
    private String path;
    @ApiModelProperty(notes = "icon图标")
    private String icon;
    @JsonSerialize(using = LongToStringSerializer.class)
    @ApiModelProperty(notes = "pid")
    private Long p_id;
    @ApiModelProperty(notes = "父级菜单")
    private String parent;
    @ApiModelProperty(notes = "是否展示在菜单栏")
    private Boolean show;
    @ApiModelProperty(notes = "是否默认选中")
    private Boolean defaultSelect;
    @ApiModelProperty(notes = "是否默认展开")
    private Boolean defaultOpen;
}
