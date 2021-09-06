package com.bootvue.admin.controller.setting.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminIn {
    private Long id;
    private String username;
    private String password;
    private String phone;
}
