package com.bootvue.core.constant;

public class AppConst {
    public static final String CAPTCHA_KEY = "captcha:line_%s";
    public static final String SMS_KEY = "code:sms_%s";

    public static final String ACCESS_TOKEN = "access_token";
    public static final String REFRESH_TOKEN = "refresh_token";

    // spring cache name
    public static final String ADMIN_CACHE = "cache:admin";
    public static final String USER_CACHE = "cache:user";
    public static final String ACTION_CACHE = "cache:action";


    // request headers
    public static final String HEADER_TENANT_ID = "tenant_id";
    public static final String HEADER_USER_ID = "user_id";
    public static final String HEADER_USERNAME = "username";
    public static final String HEADER_OPENID = "openid";
    public static final String HEADER_ROLEID = "role_id";

    //    --------------------正则
    // 手机号
    public static final String PHONE_REGEX = "^(?:(?:\\+|00)86)?1(?:(?:3[\\d])|(?:4[5-7|9])|(?:5[0-3|5-9])|(?:6[5-7])|(?:7[0-8])|(?:8[\\d])|(?:9[1|8|9]))\\d{8}$";
    // 密码
    public static final String PASSWORD_REGEX = "^\\S*(?=\\S{6,})(?=\\S*\\d)(?=\\S*[A-Z])(?=\\S*[a-z])(?=\\S*[!@#$%^&*? ])\\S*$";

    // -----------------------url
    public static final String WECHAT_CODE2SESSION = "https://api.weixin.qq.com/sns/jscode2session?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code";

}
