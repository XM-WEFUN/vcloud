package com.bootvue.auth.vo;

import com.bootvue.core.constant.AppConst;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Pattern;

@Setter
@Getter
public class PhoneParam {
    @Pattern(regexp = AppConst.PHONE_REGEX, message = "手机号错误")
    private String phone;
}
