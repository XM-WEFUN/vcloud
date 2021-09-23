package com.bootvue.auth.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@ApiModel(description = "微信小程序相关参数")
public class WechatParams {

    @ApiModelProperty(notes = "小程序login code")
    private String code;
    @ApiModelProperty(notes = "昵称")
    private String nickname;
    @ApiModelProperty(notes = "性别")
    private Integer gender;
    @ApiModelProperty(notes = "头像url")
    private String avatar;
    @ApiModelProperty(notes = "省")
    private String province;
    @ApiModelProperty(notes = "国家")
    private String country;
    @ApiModelProperty(notes = "市")
    private String city;
    private String iv;
    private String rawData;
    private String encryptedData;
    private String signature;
    @ApiModelProperty(notes = "用户风险等级 微信安全风控")
    private Integer riskRank;
}
