package com.bootvue.admin.dto;

import com.bootvue.common.model.PageIn;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel("用户查询参数")
public class UserQueryIn extends PageIn {

    @ApiModelProperty("租户id")
    private Long tenantId;

    @ApiModelProperty("账号")
    private String account;

    @ApiModelProperty("手机号")
    private String phone;

}
