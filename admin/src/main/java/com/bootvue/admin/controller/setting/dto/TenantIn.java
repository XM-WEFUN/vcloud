package com.bootvue.admin.controller.setting.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(description = "租户相关参数")
public class TenantIn {
    @ApiModelProperty(notes = "租户id")
    private Long id;
    @ApiModelProperty(notes = "租户名")
    private String name;
}
