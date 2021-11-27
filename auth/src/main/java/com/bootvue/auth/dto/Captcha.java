package com.bootvue.auth.dto;

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
@ApiModel("验证码")
public class Captcha {  // 图形验证码

    @ApiModelProperty(notes = "key", required = true)
    private String key;
    @ApiModelProperty(notes = "验证码base64图片", required = true)
    private String image;
}
