package com.bootvue.core.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName(value = "`authorization`")
public class Authorization {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 租户编号
     */
    @TableField(value = "tenant_code")
    private String tenantCode;

    /**
     * 角色
     */
    @TableField(value = "`role`")
    private String role;

    /**
     * 需要限制的uri
     */
    @TableField(value = "uri")
    private String uri;
}