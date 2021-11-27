package com.bootvue.admin.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@ApiModel("租户信息")
@Getter
@Setter
public class TenantIn {
    @ApiModelProperty(value = "租户名", required = true)
    @NotEmpty(message = "租户名不能为空")
    private String name;

    @ApiModelProperty(value = "联系人", required = true)
    @NotEmpty(message = "联系人不能为空")
    private String contactName;

    @ApiModelProperty(value = "联系方式", required = true)
    @NotEmpty(message = "联系方式不能为空")
    private String contactPhone;
}
