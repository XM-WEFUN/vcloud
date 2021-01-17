package com.bootvue.auth.service;

import com.bootvue.auth.vo.AuthResponse;
import com.bootvue.auth.vo.Credentials;
import com.bootvue.auth.vo.PhoneParam;

public interface AuthService {
    AuthResponse authentication(Credentials credentials);  // 用户登录认证 或 刷新token

    void handleSmsCode(PhoneParam phoneParam);
}
