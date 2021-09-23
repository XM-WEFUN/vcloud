package com.bootvue.admin.controller.setting.dto;

import com.bootvue.core.serializer.LongToStringSerializer;
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
@ApiModel(description = "角色信息")
public class RoleListOut {

    @JsonSerialize(using = LongToStringSerializer.class)
    @ApiModelProperty(notes = "角色id")
    private Long id;
    @ApiModelProperty(notes = "角色名")
    private String roleName;
    @JsonSerialize(using = LongToStringSerializer.class)
    @ApiModelProperty(notes = "租户id")
    private Long tenantId;
    @ApiModelProperty(notes = "租户名")
    private String tenantName;
}
