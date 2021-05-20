package com.bootvue.admin.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class UserRoleIn {
    @NotNull(message = "用户id不能为空")
    private Long userId;
    @NotEmpty(message = "角色名不能为空")
    private String roleName;
}
