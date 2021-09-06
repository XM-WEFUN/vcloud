package com.bootvue.core.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "`role_admin`")
public class RoleAdmin {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField(value = "role_id")
    private Long roleId; // 角色id

    @TableField(value = "admin_id")
    private Long adminId; // 管理员用户id
}
