package com.bootvue.admin.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel("菜单返回类型")
public class TypeIn {

    @ApiModelProperty("0 普通模式 1 返回菜单树型结构(包含按钮) 2 返回菜单树型结构(不包含按钮)")
    private Integer type = 0;
}
