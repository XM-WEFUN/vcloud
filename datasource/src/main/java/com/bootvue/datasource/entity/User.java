package com.bootvue.datasource.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.bootvue.datasource.type.AccountTypeEnum;
import com.bootvue.datasource.type.GenderEnum;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户表
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class User implements Serializable {
    private static final long serialVersionUID = 9036773750958954304L;

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 租户id
     */
    private Long tenantId;

    /**
     * 账号类型 0普通用户 1租户 2平台
     */
    private AccountTypeEnum type;

    /**
     * 账号
     */
    private String account;

    /**
     * 密码
     */
    private String password;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 性别 1男 2女 0未知
     */
    private GenderEnum gender;

    /**
     * 国家
     */
    private String country;

    /**
     * 省份
     */
    private String province;

    /**
     * 市
     */
    private String city;

    /**
     * 区
     */
    private String region;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 状态 0禁用  1正常
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