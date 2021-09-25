package com.bootvue.admin.controller.setting.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel("角色相关参数")
public class RoleIn {

    @ApiModelProperty(notes = "角色id")
    private Long id;
    @ApiModelProperty(notes = "租户id")
    private Long tenantId;
    @ApiModelProperty(notes = "角色名")
    private String roleName;
}
