package com.bootvue.admin.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@ApiModel("用户信息")
public class UserProfile implements Serializable {

    private static final long serialVersionUID = -4318348103898053152L;

    @ApiModelProperty("账号")
    private String account;
    @ApiModelProperty("昵称")
    private String nickName;
    @ApiModelProperty("头像")
    private String avatar;
    @ApiModelProperty("性别")
    private String gender;
    @ApiModelProperty("手机号")
    private String phone;
    @ApiModelProperty("菜单")
    private List<MenuItem> menus;
}
