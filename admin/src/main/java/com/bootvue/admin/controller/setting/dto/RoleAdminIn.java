package com.bootvue.admin.controller.setting.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.Set;

@Getter
@Setter
public class RoleAdminIn {

    @NotNull(message = "角色id不能为空")
    @ApiModelProperty(notes = "角色id")
    private Long roleId;
    @ApiModelProperty(notes = "管理员用户 id集合")
    private Set<Long> ids;
}
