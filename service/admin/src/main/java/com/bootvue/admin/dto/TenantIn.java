package com.bootvue.admin.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@ApiModel("租户信息")
@Getter
@Setter
public class TenantIn extends Id {
    @ApiModelProperty(value = "租户名")
    private String name;

    @ApiModelProperty(value = "联系人")
    private String contactName;

    @ApiModelProperty(value = "联系方式")
    private String contactPhone;
}
