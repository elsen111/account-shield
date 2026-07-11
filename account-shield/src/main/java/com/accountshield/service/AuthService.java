package com.accountshield.service;

import com.accountshield.dto.auth.LoginRequest;
import com.accountshield.dto.auth.TokenResponse;
import com.accountshield.dto.auth.RegisterRequest;
import com.accountshield.dto.common.UserResponse;

public interface AuthService {

    UserResponse register(RegisterRequest request);

    TokenResponse login(LoginRequest request);

}
