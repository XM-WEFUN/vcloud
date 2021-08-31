package com.bootvue.auth.service;

import com.bootvue.auth.dto.AuthResponse;
import com.bootvue.auth.dto.CaptchaResponse;
import com.bootvue.auth.dto.Credentials;
import com.bootvue.auth.dto.PhoneParams;

public interface AuthService {
    AuthResponse authentication(Credentials credentials);  // 用户登录认证 或 刷新token

    void handleSmsCode(PhoneParams phoneParams);

    CaptchaResponse getCaptcha();
}
