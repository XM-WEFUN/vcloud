package com.bootvue.core.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "`user`")
public class User implements Serializable {

    private static final long serialVersionUID = 1335947452185951815L;

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
     * 手机号, 不同租户下手机号可以重复
     */
    @TableField(value = "phone")
    private String phone;

    /**
     * 微信openid
     */
    @TableField(value = "openid")
    private String openid;

    /**
     * 密码
     */
    @TableField(value = "`password`")
    private String password;

    /**
     * 昵称
     */
    @TableField(value = "nickname")
    private String nickname;

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
     * 0:禁用 1:正常
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