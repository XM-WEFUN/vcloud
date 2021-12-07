package com.bootvue.datasource.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.bootvue.datasource.type.DeptTypeEnum;
import lombok.*;
import lombok.experimental.Accessors;

/**
 * 部门表
 */
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class Dept {

    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 租户id
     */
    private Long tenantId;

    /**
     * 部门名称
     */
    private String name;

    /**
     * 类型 0 公司部门 1 机构/公司 2 小组 3 其它
     */
    private DeptTypeEnum type;

    /**
     * 顺序
     */
    private Integer sort;

    /**
     * 上级id 顶级为0
     */
    private Long pId;

    /**
     * 联系人
     */
    private String contactName;

    /**
     * 联系方式
     */
    private String contactPhone;

    /**
     * 备注
     */
    private String remark;
}