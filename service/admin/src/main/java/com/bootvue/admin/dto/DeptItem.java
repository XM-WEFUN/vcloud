package com.bootvue.admin.dto;

import com.bootvue.common.serializer.LongToStringSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ApiModel("部门信息")
public class DeptItem {

    @JsonSerialize(using = LongToStringSerializer.class)
    private Long id;

    @JsonSerialize(using = LongToStringSerializer.class)
    @ApiModelProperty("租户id")
    @NotNull(message = "租户id不能为空")
    private Long tenantId;

    @ApiModelProperty("租户名称")
    private String tenantName;

    @ApiModelProperty("部门名称")
    @NotEmpty(message = "名称不能为空")
    private String name;

    @ApiModelProperty("类型 0 公司部门 1机构/公司 2小组 3其它")
    @NotNull(message = "类型不能为空")
    private Integer type;

    @ApiModelProperty("顺序")
    private Integer sort;

    @JsonSerialize(using = LongToStringSerializer.class)
    @ApiModelProperty("上级id")
    private Long pid;

    @ApiModelProperty("上级部门名称")
    private String pname;

    @ApiModelProperty("联系人")
    private String contactName;

    @ApiModelProperty("联系方式")
    private String contactPhone;

    @ApiModelProperty("备注")
    private String remark;

}
