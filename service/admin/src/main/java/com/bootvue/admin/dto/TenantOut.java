package com.bootvue.admin.dto;

import com.bootvue.common.serializer.LongToStringSerializer;
import com.bootvue.datasource.model.Base;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("租户信息")
public class TenantOut extends Base {

    @JsonSerialize(using = LongToStringSerializer.class)
    private Long id;

    @ApiModelProperty("租户编号")
    private String code;
    @ApiModelProperty("租户名")
    private String name;
    @ApiModelProperty("联系人")
    private String contactName;
    @ApiModelProperty("联系方式")
    private String contactPhone;
    @ApiModelProperty("状态")
    private Boolean status;
}
