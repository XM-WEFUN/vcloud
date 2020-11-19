package com.bootvue.core.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "`user`")
public class User {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 租户编号
     */
    @TableField(value = "tenant_code")
    private String tenantCode;

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
     * 角色 逗号分隔
     */
    @TableField(value = "roles")
    private String roles;

    /**
     * 0:正常 1:禁用
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