package com.bootvue.admin.controller.setting.dto;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel("角色相关参数")
public class RoleIn {
    private Long id;
    private String name;
}
