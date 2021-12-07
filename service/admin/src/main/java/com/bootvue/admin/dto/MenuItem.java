package com.bootvue.admin.dto;

import com.bootvue.common.serializer.LongToStringSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("菜单")
public class MenuItem implements Serializable {

    private static final long serialVersionUID = 2752549007939109954L;

    @JsonSerialize(using = LongToStringSerializer.class)
    private Long id;

    @ApiModelProperty("标题/按钮名")
    @NotEmpty(message = "标题不能为空")
    private String title;

    @ApiModelProperty("上级菜单id")
    @JsonSerialize(using = LongToStringSerializer.class)
    private Long pid;

    @ApiModelProperty("上级菜单名称")
    private String ptitle;

    @ApiModelProperty("唯一标识")
    private String key;

    @ApiModelProperty("前端路由")
    private String path;

    @ApiModelProperty("icon图标")
    private String icon;

    @ApiModelProperty("排序")
    private Integer sort;

    @ApiModelProperty("类型 0菜单 1按钮")
    @NotNull(message = "类型不能为空")
    private Integer type;

    @ApiModelProperty("权限字段")
    private String action;

    @ApiModelProperty("菜单栏是否展示")
    private Boolean show;

    @ApiModelProperty("是否默认选中")
    private Boolean defaultSelect;

    @ApiModelProperty("二级菜单是否默认展开")
    private Boolean defaultOpen;

    @ApiModelProperty("子菜单")
    private List<MenuItem> children;
}
