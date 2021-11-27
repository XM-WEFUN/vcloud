package com.bootvue.common.constant;

public class AppConst {
    // ****************** 运营平台租户信息 **********
    public static final Long ADMIN_TENANT_ID = 1L;

    // ****************** 常量
    public static final String CAPTCHA_KEY = "captcha:line_%s";
    public static final String SMS_KEY = "code:sms_%s";

    public static final String ACCESS_TOKEN = "access_token";
    public static final String REFRESH_TOKEN = "refresh_token";

    //******************* redis cache name
    public static final String ADMIN_CACHE = "cache:admin";

    //******************* token 请求头
    public static final String REQUEST_HEADER_TOKEN = "Authorization";


    //******************正则
    // 账号 字母开头 字母数字_ 5-12位
    public static final String ACCOUNT_REGEX = "/^[a-zA-Z]\\w{4,11}$/";
    // 手机号
    public static final String PHONE_REGEX = "^(?:(?:\\+|00)86)?1(?:(?:3[\\d])|(?:4[5-7|9])|(?:5[0-3|5-9])|(?:6[5-7])|(?:7[0-8])|(?:8[\\d])|(?:9[1|8|9]))\\d{8}$";
    // 密码 至少6位，包括至少1个大写字母，1个小写字母，1个数字，1个特殊字符
    public static final String PASSWORD_REGEX = "^\\S*(?=\\S{6,})(?=\\S*\\d)(?=\\S*[A-Z])(?=\\S*[a-z])(?=\\S*[!@#$%^&*? ])\\S*$";

}
