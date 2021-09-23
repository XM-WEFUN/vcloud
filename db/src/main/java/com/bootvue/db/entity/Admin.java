package com.bootvue.db.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@TableName(value = "`admin`")
public class Admin implements Serializable {
    private static final long serialVersionUID = 3065915125297873393L;

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    @TableField(value = "tenant_id")
    private Long tenantId;

    /**
     * 用户名
     */
    @TableField(value = "username")
    private String username;

    /**
     * 手机号
     */
    @TableField(value = "phone")
    private String phone;

    /**
     * 密码
     */
    @TableField(value = "`password`")
    private String password;

    /**
     * 头像
     */
    @TableField(value = "avatar")
    private String avatar;

    /**
     * 账号状态 0:禁用  1:正常
     */
    @TableField(value = "`status`")
    private Boolean status;

    @TableField(value = "create_time")
    private LocalDateTime createTime;

    @TableField(value = "update_time")
    private LocalDateTime updateTime;

    @TableField(value = "delete_time")
    private LocalDateTime deleteTime;
}