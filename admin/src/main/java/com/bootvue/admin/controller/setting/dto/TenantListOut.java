package com.bootvue.admin.controller.setting.dto;

import com.bootvue.core.serializer.LocalDateTimeSerializer;
import com.bootvue.core.serializer.LongToStringSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "租户信息")
public class TenantListOut {

    @JsonSerialize(using = LongToStringSerializer.class)
    @ApiModelProperty(notes = "租户id")
    private Long id;
    @ApiModelProperty(notes = "租户名")
    private String name;
    @JsonSerialize(using = LocalDateTimeSerializer.class, nullsUsing = LocalDateTimeSerializer.class)
    private LocalDateTime createTime;
}
