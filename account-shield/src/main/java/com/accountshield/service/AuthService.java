package com.accountshield.service;

import com.accountshield.dto.auth.*;
import com.accountshield.dto.common.UserResponse;

public interface AuthService {

    UserResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refresh(RefreshTokenRequest request);

    void logout();

    void verifyEmail(VerifyEmailRequest request);

    void resendVerification(ResendVerificationRequest request);

}
