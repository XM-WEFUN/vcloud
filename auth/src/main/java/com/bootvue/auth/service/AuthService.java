package com.bootvue.auth.service;

import com.bootvue.auth.dto.Captcha;
import com.bootvue.auth.dto.ClientInfo;

public interface AuthService {
    Captcha getCaptcha(ClientInfo clientInfo);
}
