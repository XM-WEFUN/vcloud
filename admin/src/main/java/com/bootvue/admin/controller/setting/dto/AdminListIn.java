package com.bootvue.admin.controller.setting.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminListIn {
    private Long current = 1L;
    private Long pageSize = 10L;
    private String username;
    private String phone;
}
