package com.bootvue.admin.service;

import com.bootvue.common.model.PageIn;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel("查询参数")
public class Oauth2QueryIn extends PageIn {

    @ApiModelProperty("client id")
    private String clientId;
}
