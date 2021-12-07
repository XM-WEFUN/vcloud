package com.bootvue.admin.dto;

import com.bootvue.common.model.PageIn;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel("角色查询参数")
public class RoleQueryIn extends PageIn {

    @ApiModelProperty("角色名")
    private String name;

    @ApiModelProperty("租户id")
    private Long tenantId;
}
