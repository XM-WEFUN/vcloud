package com.bootvue.core.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.bootvue.core.constant.GenderEnum;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "`user`")
public class User implements Serializable {
    private static final long serialVersionUID = 2112159784340829390L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 租户id
     */
    @TableField(value = "tenant_id")
    private Long tenantId;

    /**
     * 用户名 昵称
     */
    @TableField(value = "username")
    private String username;

    /**
     * 小程序openid
     */
    @TableField(value = "openid")
    private String openid;

    /**
     * 手机号
     */
    @TableField(value = "phone")
    private String phone;

    /**
     * 头像
     */
    @TableField(value = "avatar")
    private String avatar;

    /**
     * 性别 0:未知 1:男 2:女
     */
    @TableField(value = "gender")
    private GenderEnum gender;

    /**
     * 国家
     */
    @TableField(value = "country")
    private String country;

    /**
     * 省
     */
    @TableField(value = "province")
    private String province;

    /**
     * 城市
     */
    @TableField(value = "city")
    private String city;

    /**
     * 状态 0: 禁用 1: 正常
     */
    @TableField(value = "`status`")
    private Boolean status;

    /**
     * 备注, 记录禁用原因等
     */
    @TableField(value = "remark")
    private String remark;

    @TableField(value = "create_time")
    private LocalDateTime createTime;

    @TableField(value = "update_time")
    private LocalDateTime updateTime;
}