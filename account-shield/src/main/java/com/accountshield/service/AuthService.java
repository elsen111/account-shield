package com.accountshield.service;

import com.accountshield.dto.auth.LoginRequest;
import com.accountshield.dto.auth.RefreshTokenRequest;
import com.accountshield.dto.auth.AuthResponse;
import com.accountshield.dto.auth.RegisterRequest;
import com.accountshield.dto.common.UserResponse;

public interface AuthService {

    UserResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refresh(RefreshTokenRequest request);

    void logout();

}
