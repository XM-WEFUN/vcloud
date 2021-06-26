package com.bootvue.admin.controller.menu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MenuIn {
    private Long id;
    private Long tenantId;
    private String title;
    private Integer sort;
    private String key;
    private String path;
    private String icon;
    private Long pid;
    private String actions;
    private Integer defaultSelect;
    private Integer defaultOpen;
}
