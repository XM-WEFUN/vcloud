package com.bootvue.admin.controller.setting.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.Set;

@Getter
@Setter
public class AdminRoleIn {

    @NotNull(message = "用户id不能为空")
    @ApiModelProperty(notes = "管理员id")
    private Long adminId;
    @ApiModelProperty(notes = "role id集合")
    private Set<Long> ids;
}
