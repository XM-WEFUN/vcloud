package com.bootvue.admin.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@ApiModel("通用对象id")
@Getter
@Setter
public class Id {
    @ApiModelProperty(value = "id")
    private Long id;
}
