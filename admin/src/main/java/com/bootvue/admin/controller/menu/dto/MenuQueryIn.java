package com.bootvue.admin.controller.menu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MenuQueryIn {
    private Long current = 1L;
    private Long pageSize = 10L;

}
