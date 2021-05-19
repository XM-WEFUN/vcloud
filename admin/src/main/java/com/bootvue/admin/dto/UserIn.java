package com.bootvue.admin.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel("新增/更新管理员用户入参")
public class UserIn {

    @ApiModelProperty(notes = "更新时id必传")
    private Long id;
    private String username;
    private String password;
    private String phone;
    private Long roleId;
}
