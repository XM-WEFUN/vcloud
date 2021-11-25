package com.bootvue.datasource.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.bootvue.datasource.type.MenuTypeEnum;
import lombok.*;

/**
 * 菜单/按钮表
 */
@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Menu {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 菜单/按钮名称
     */
    private String title;

    /**
     * 唯一标识
     */
    private String key;

    /**
     * 前端路由
     */
    private String path;

    /**
     * icon图标
     */
    private String icon;

    /**
     * 顺序
     */
    private Integer sort;

    /**
     * 上级id  没有上级为0
     */
    private Long pId;

    /**
     * 类型 0 菜单 1按钮
     */
    private MenuTypeEnum type;

    /**
     * 权限字段
     */
    private String action;

    /**
     * 菜单栏是否展示 0 不显示 1显示
     */
    private Boolean show;

    /**
     * 是否默认选择 0否  1是
     */
    private Boolean defaultSelect;

    /**
     * 二级菜单是否默认展开  0 否 1是
     */
    private Boolean defaultOpen;
}