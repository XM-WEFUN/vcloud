package com.bootvue.admin.controller;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("demo测试")
public class Demo {

    @ApiModelProperty(notes = "字符串集合", required = true)
    private List<String> tt;
    @ApiModelProperty(notes = "时间", required = true)
    private LocalDateTime time;
}
