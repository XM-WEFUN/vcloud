package com.bootvue.admin.dto;

import com.bootvue.common.model.PageIn;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel("租户查询参数")
public class TenantQueryIn extends PageIn {
    @ApiModelProperty("租户名")
    private String name;
    @ApiModelProperty("租户编号")
    private String code;
}
