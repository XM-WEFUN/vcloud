package com.bootvue.datasource.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 租户表
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class Tenant implements Serializable {
    private static final long serialVersionUID = -3161519350405977674L;

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 租户编号
     */
    private String code;

    /**
     * 租户名称
     */
    private String name;

    /**
     * 联系人
     */
    private String contactName;

    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 状态 0 正常   1 禁用
     */
    private Boolean status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 删除时间
     */
    private LocalDateTime deleteTime;
}