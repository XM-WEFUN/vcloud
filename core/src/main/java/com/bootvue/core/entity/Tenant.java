package com.bootvue.core.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
@TableName(value = "tenant")
public class Tenant {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 租户编号
     */
    @TableField(value = "code")
    private String code;

    /**
     * 租户名称
     */
    @TableField(value = "`name`")
    private String name;

    @TableField(value = "create_time")
    private LocalDateTime createTime;

    @TableField(value = "update_time")
    private LocalDateTime updateTime;

    /**
     * 删除时需要同步删除租户下的用户
     */
    @TableField(value = "delete_time")
    private LocalDateTime deleteTime;
}