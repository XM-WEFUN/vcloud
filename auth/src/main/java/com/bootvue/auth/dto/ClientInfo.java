package com.bootvue.auth.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "客户端参数")
public class ClientInfo {
    @ApiModelProperty(notes = "租户编号", required = true)
    @NotEmpty(message = "租户编号不能为空")
    private String tenantCode;

    @ApiModelProperty(notes = "client_id", required = true)
    @NotEmpty(message = "client_id不能为空")
    private String clientId;

    @ApiModelProperty(notes = "secret", required = true)
    @NotEmpty(message = "secret不能为空")
    private String secret;

    @ApiModelProperty(notes = "平台类型 0 WEB 1 APP 2 小程序", required = true)
    @NotNull(message = "平台类型不能为空")
    private Integer platform;
}
