package com.bootvue.admin.controller.setting.dto;

import com.bootvue.core.serializer.LongToStringSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "一级菜单信息")
public class MenuParentList {

    @JsonSerialize(using = LongToStringSerializer.class)
    @ApiModelProperty(notes = "菜单id")
    private Long id;
    @ApiModelProperty(notes = "菜单标题")
    private String title;
}
