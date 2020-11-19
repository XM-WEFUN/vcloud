package com.bootvue.auth.service;

import com.bootvue.auth.vo.AuthResponse;
import com.bootvue.auth.vo.Credentials;

public interface AuthService {
    AuthResponse authentication(Credentials credentials);
}
