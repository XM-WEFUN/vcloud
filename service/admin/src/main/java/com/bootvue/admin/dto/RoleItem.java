package com.bootvue.admin.dto;

import com.bootvue.common.serializer.LongToStringSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("角色信息")
public class RoleItem {

    @JsonSerialize(using = LongToStringSerializer.class)
    private Long id;

    @JsonSerialize(using = LongToStringSerializer.class)
    @ApiModelProperty("租户id")
    private Long tenantId;

    @ApiModelProperty("租户名")
    private String tenantName;

    @ApiModelProperty("角色名")
    private String name;

    @ApiModelProperty("权限字段")
    private String action;
}
