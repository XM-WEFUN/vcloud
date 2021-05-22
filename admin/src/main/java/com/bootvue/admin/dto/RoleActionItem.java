package com.bootvue.admin.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import java.util.Set;

@Getter
@Setter
@ToString
public class RoleActionItem {
    @NotEmpty(message = "key不能为空")
    private String key;

    private Set<String> actions;
}
