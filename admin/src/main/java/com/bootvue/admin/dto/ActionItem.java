package com.bootvue.admin.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("menu菜单与对应权限信息")
public class ActionItem {
    @ApiModelProperty(notes = "菜单名称")
    private String title;
    private String key;
    @ApiModelProperty(notes = "权限--对应前端checkbox-group [{label: '查看', value: 'user:list'}]")
    private List<OptionItem> options;

    @ApiModelProperty(notes = "checkbox 用户已有的value ['user:add','action:list,action:add']")
    private Set<String> checked;
    private List<ActionItem> children;
}
