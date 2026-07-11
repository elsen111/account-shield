package com.accountshield.service;

import com.accountshield.dto.auth.AuthResponse;
import com.accountshield.entity.RefreshTokenEntity;
import com.accountshield.entity.UserEntity;

public interface RefreshTokenService {

    RefreshTokenEntity create(UserEntity user);

    RefreshTokenEntity verify(String token);

    RefreshTokenEntity rotate(RefreshTokenEntity token);

    void revoke(RefreshTokenEntity token);

    AuthResponse refresh(String refreshToken);

    void revokeAll(UserEntity user);

}