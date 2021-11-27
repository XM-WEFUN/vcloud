package com.bootvue.auth.dto;

import com.bootvue.datasource.type.PlatformEnum;
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
@ApiModel("客户端参数")
public class ClientInfo {
    @ApiModelProperty(value = "租户编号", required = true)
    @NotEmpty(message = "租户编号不能为空")
    private String tenant_code;

    @ApiModelProperty(value = "client_id", required = true)
    @NotEmpty(message = "client_id不能为空")
    private String client_id;

    @ApiModelProperty(value = "secret", required = true)
    @NotEmpty(message = "secret不能为空")
    private String secret;

    @ApiModelProperty(value = "平台类型", required = true)
    @NotNull(message = "平台类型不能为空")
    private PlatformEnum platform;
}
