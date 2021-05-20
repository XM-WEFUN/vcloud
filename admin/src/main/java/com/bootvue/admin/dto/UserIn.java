package com.bootvue.admin.dto;

import com.bootvue.core.constant.AppConst;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Pattern;

@Getter
@Setter
@ApiModel("新增/更新管理员用户入参")
public class UserIn {

    @ApiModelProperty(notes = "更新时id必传")
    private Long id;
    private String username;
    @Pattern(regexp = AppConst.PASSWORD_REGEX, message = "密码强度太弱")
    private String password;
    private String phone;
    private Long roleId;
}
