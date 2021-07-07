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
    private static final long serialVersionUID = -8310905773465850691L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField(value = "tenant_id")
    private Long tenantId;

    @TableField(value = "username")  // 昵称
    private String username;

    @TableField(value = "openid")
    private String openid;

    @TableField(value = "phone")
    private String phone;

    @TableField(value = "avatar")
    private String avatar;

    @TableField(value = "gender")
    private GenderEnum gender;  // 性别  0未知  1男  2女

    @TableField(value = "country")
    private String country;

    @TableField(value = "province")
    private String province;

    @TableField(value = "city")
    private String city;

    @TableField(value = "status")
    private Boolean status;

    @TableField(value = "remark")
    private String remark;

    @TableField(value = "create_time")
    private LocalDateTime createTime;

    @TableField(value = "update_time")
    private LocalDateTime updateTime;
}
