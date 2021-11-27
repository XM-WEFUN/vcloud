package com.bootvue.auth.service;

import com.bootvue.auth.dto.AuthParam;
import com.bootvue.auth.dto.AuthResponse;
import com.bootvue.auth.dto.Captcha;
import com.bootvue.auth.dto.ClientInfo;

public interface AuthService {
    Captcha getCaptcha(ClientInfo clientInfo);

    AuthResponse token(AuthParam param);
}
