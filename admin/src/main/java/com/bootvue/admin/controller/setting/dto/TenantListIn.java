package com.bootvue.admin.controller.setting.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(description = "租户查询参数")
public class TenantListIn {

    private Long current = 1L;
    private Long pageSize = 10L;

    @ApiModelProperty(notes = "租户名")
    private String name;
}
