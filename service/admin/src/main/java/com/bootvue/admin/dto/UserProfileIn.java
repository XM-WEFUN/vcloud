package com.bootvue.admin.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel("用户信息")
public class UserProfileIn {
    @ApiModelProperty("昵称")
    private String nickName;
    @ApiModelProperty("性别")
    private Integer gender;
    @ApiModelProperty("手机号")
    private String phone;
    @ApiModelProperty("密码")
    private String password;
}
