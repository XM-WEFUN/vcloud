package com.bootvue.admin.dto;

import com.bootvue.common.serializer.LongToStringSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
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

    @JsonSerialize(using = LongToStringSerializer.class)
    @ApiModelProperty("用户所属租户id")
    private Long tenantId;

    @ApiModelProperty("账号类型 0 普通用户 1 租户账号 2平台账号")
    private Integer type;

    @ApiModelProperty("账号")
    private String account;

    @ApiModelProperty("昵称")
    private String nickName;

    @ApiModelProperty("头像")
    private String avatar;

    @ApiModelProperty("性别 0未知 1男 2女")
    private Integer gender;

    @ApiModelProperty("手机号")
    private String phone;

    @ApiModelProperty("菜单")
    private List<MenuItem> menus;
}
