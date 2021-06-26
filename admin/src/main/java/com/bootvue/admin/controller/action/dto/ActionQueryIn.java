package com.bootvue.admin.controller.action.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ActionQueryIn {
    private Long current = 1L;
    private Long pageSize = 10L;
    private String api;
}
