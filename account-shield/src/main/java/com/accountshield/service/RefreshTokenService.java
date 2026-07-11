package com.accountshield.service;

import com.accountshield.dto.auth.TokenResponse;
import com.accountshield.entity.RefreshTokenEntity;
import com.accountshield.entity.UserEntity;

public interface RefreshTokenService {

    RefreshTokenEntity create(UserEntity user);

    RefreshTokenEntity verify(String token);

    RefreshTokenEntity rotate(RefreshTokenEntity token);

    void revoke(RefreshTokenEntity token);

    TokenResponse refresh(String refreshToken);

    void revokeAll(UserEntity user);

}