package com.bootvue.admin.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Setter
@Getter
public class RoleActionIn {
    @NotNull(message = "role id不能为空")
    private Long roleId;

    @NotEmpty(message = "参数不能为空")
    private Set<RoleActionItem> changedItems;

    private String roleName;
}
