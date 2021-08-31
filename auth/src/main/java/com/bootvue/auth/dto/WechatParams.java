package com.bootvue.auth.dto;

import io.swagger.annotations.ApiModel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@ApiModel(description = "微信小程序相关参数")
public class WechatParams {

    private String code;
    private String nickname;
    private Integer gender;
    private String avatar;
    private String province;
    private String country;
    private String city;
    private String iv;
    private String rawData;
    private String encryptedData;
    private String signature;
    private Integer riskRank; // 用户风险等级 微信安全风控
}
