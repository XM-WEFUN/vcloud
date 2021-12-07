package com.bootvue.datasource.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * 角色表
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class Role {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 租户id
     */
    private Long tenantId;

    /**
     * 角色名
     */
    private String name;

    /**
     * 权限字段
     */
    private String action;
}