package com.bootvue.admin.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.Set;

@Getter
@Setter
public class AdminRolesIn {
    private Set<Long> selectedKeys;
    private Set<Long> unSelectedKeys;

    @NotNull(message = "角色id不能为空")
    private Long roleId;
}
