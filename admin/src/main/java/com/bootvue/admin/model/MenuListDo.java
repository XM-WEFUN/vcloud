package com.bootvue.admin.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MenuListDo {
    private Long id;
    private String title;
    private Integer sort;
    private String key;
    private String path;
    private String icon;
    private Long pId;
    private String parent;
    private Boolean show;
    private Boolean defaultSelect;
    private Boolean defaultOpen;
}
