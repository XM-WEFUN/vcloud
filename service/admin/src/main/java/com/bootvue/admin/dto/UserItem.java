package com.bootvue.admin.dto;

import com.bootvue.common.serializer.LongToStringSerializer;
import com.bootvue.datasource.model.Base;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel("用户信息")
public class UserItem extends Base {

    @JsonSerialize(using = LongToStringSerializer.class)
    @ApiModelProperty("租户id")
    private Long tenantId;

    @ApiModelProperty("租户名")
    private String tenantName;

    @ApiModelProperty("账号")
    private String account;

    @ApiModelProperty("密码")
    private String password;

    @ApiModelProperty("昵称")
    private String nickName;

    @ApiModelProperty("手机号")
    private String phone;

    @ApiModelProperty("性别 0 未知 1男 2女")
    private Integer gender;

    @ApiModelProperty("国家")
    private String country;

    @ApiModelProperty("省")
    private String province;

    @ApiModelProperty("市")
    private String city;

    @ApiModelProperty("区")
    private String region;

    @ApiModelProperty("头像")
    private String avatar;

    @ApiModelProperty("用户状态")
    private Boolean status;

    @ApiModelProperty("用户角色 ,分割")
    private String roles;
}
