package com.bootvue.core.ddo.menu;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MenuTenantDo {
    private Long id;
    private String tenantName;
    private Long tenantId;
    private String title;
    private Integer sort;
    private String key;
    private String path;
    private String icon;
    private Long pid;
    private String ptitle;
    private String actions;
    private Integer defaultSelect;
    private Integer defaultOpen;
}
