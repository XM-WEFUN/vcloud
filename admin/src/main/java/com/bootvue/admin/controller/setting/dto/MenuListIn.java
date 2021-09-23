package com.bootvue.admin.controller.setting.dto;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel(description = "菜单查询参数")
public class MenuListIn {

    private Long current = 1L;
    private Long pageSize = 10L;
}
