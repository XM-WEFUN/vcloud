package com.bootvue.core.constant;

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
    public static final String WECHAT_USER_CACHE = "cache:wechat-user";


    //******************* jwt token属性名
    public static final String TOKEN_USER_ID = "id";
    public static final String TOKEN_TENANT_ID = "tenant_id";
    public static final String TOKEN_USERNAME = "username";
    public static final String TOKEN_TYPE = "type";
    public static final String TOKEN_LABEL = "label";

    //    --------------------正则
    // 账号 字母开头 字母数字_ 5-16位
    public static final String ACCOUNT_REGEX = "/^[a-zA-Z]\\w{4,15}$/";
    // 手机号
    public static final String PHONE_REGEX = "^(?:(?:\\+|00)86)?1(?:(?:3[\\d])|(?:4[5-7|9])|(?:5[0-3|5-9])|(?:6[5-7])|(?:7[0-8])|(?:8[\\d])|(?:9[1|8|9]))\\d{8}$";
    // 密码 至少6位，包括至少1个大写字母，1个小写字母，1个数字，1个特殊字符
    public static final String PASSWORD_REGEX = "^\\S*(?=\\S{6,})(?=\\S*\\d)(?=\\S*[A-Z])(?=\\S*[a-z])(?=\\S*[!@#$%^&*? ])\\S*$";

    // -----------------------url
    public static final String WECHAT_CODE2SESSION = "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code";

}
