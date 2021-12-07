package com.bootvue.admin.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.util.Set;

@Getter
@Setter
@ApiModel("分配参数")
public class AssignIn {

    @ApiModelProperty("分配的id 集合")
    private Set<Long> ids;

    @ApiModelProperty("对应分配资源的id")
    @NotNull(message = "id不能为空")
    private Long id;
}
