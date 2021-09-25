package com.bootvue.admin.controller.setting.dto;

import com.bootvue.core.constant.AppConst;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@ApiModel(description = "管理员相关参数")
public class AdminIn {

    private Long id;
    @ApiModelProperty(notes = "租户id")
    private Long tenantId;
    @ApiModelProperty(notes = "用户名")
    private String username;
    @ApiModelProperty(notes = "密码")
    private String password;
    @ApiModelProperty(notes = "手机号")
    private String phone;
    @NotNull(message = "客户端类型不能为空")
    @ApiModelProperty(notes = "客户端类型 0: web 1:小程序", required = true)
    private Integer platform;
}
