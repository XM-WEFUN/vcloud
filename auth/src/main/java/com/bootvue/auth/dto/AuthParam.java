package com.bootvue.auth.dto;

import com.bootvue.datasource.type.GrantTypeEnum;
import com.bootvue.datasource.type.PlatformEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ApiModel("认证参数")
public class AuthParam {
    @ApiModelProperty("grant type=code时 获取的一次性临时code")
    private String code;

    @ApiModelProperty(value = "租户编号", required = true)
    @NotEmpty(message = "租户编号不能为空")
    private String tenantCode;

    @ApiModelProperty(value = "client id", required = true)
    @NotEmpty(message = "client id不能为空")
    private String clientId;

    @ApiModelProperty(value = "secret", required = true)
    @NotEmpty(message = "secret不能为空")
    private String secret;

    @ApiModelProperty("账号")
    private String account;

    @ApiModelProperty("密码 公钥加密")
    private String password;

    @ApiModelProperty("验证码唯一key")
    private String key;

    @ApiModelProperty("验证码值")
    private String captcha;

    @ApiModelProperty("refresh token")
    private String refreshToken;

    @ApiModelProperty(value = "认证类型", required = true)
    @NotNull(message = "grant type不能为空")
    private GrantTypeEnum grantType;

    @ApiModelProperty(value = "平台类型", required = true)
    @NotNull(message = "平台类型不能为空")
    private PlatformEnum platform;
}
