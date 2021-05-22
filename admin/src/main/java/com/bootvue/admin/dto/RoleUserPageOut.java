package com.bootvue.admin.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class RoleUserPageOut<T> {
    private Long total;
    private Set<Long> keys;  // user id集合
    private T rows;
}
