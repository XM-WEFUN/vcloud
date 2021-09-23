package com.bootvue.admin.controller.setting.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(description = "管理员相关参数in")
public class AdminListIn {

    private Long current = 1L;
    private Long pageSize = 10L;
    @ApiModelProperty(notes = "用户名")
    private String username;
    @ApiModelProperty(notes = "手机号")
    private String phone;
}
