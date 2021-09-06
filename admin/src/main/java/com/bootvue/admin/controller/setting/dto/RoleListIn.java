package com.bootvue.admin.controller.setting.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(description = "角色查询参数")
public class RoleListIn {
    private Long current = 1L;
    private Long pageSize = 10L;

    @ApiModelProperty(notes = "角色名")
    private String name;
}
