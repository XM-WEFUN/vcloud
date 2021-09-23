package com.bootvue.admin.controller.setting.dto;

import com.bootvue.core.serializer.LocalDateTimeSerializer;
import com.bootvue.core.serializer.LongToStringSerializer;
import com.bootvue.db.entity.Role;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(description = "管理员用户参数out")
public class AdminListOut {

    @JsonSerialize(using = LongToStringSerializer.class)
    @ApiModelProperty(notes = "id")
    private Long id;
    @ApiModelProperty(notes = "用户名")
    private String username;
    @ApiModelProperty(notes = "手机号")
    private String phone;
    @ApiModelProperty(notes = "状态 true:正常 false:禁用")
    private Boolean status;
    @ApiModelProperty(notes = "租户id")
    private String tenantId;
    @ApiModelProperty(notes = "租户名")
    private String tenantName;
    @ApiModelProperty(notes = "角色")
    private List<Role> roles;
    @JsonSerialize(using = LocalDateTimeSerializer.class, nullsUsing = LocalDateTimeSerializer.class)
    private LocalDateTime createTime;
    @JsonSerialize(using = LocalDateTimeSerializer.class, nullsUsing = LocalDateTimeSerializer.class)
    private LocalDateTime deleteTime;
}

