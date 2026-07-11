package com.accountshield.service;


import com.accountshield.dto.auth.TokenResponse;
import com.accountshield.entity.RefreshTokenEntity;
import com.accountshield.entity.UserEntity;
import com.accountshield.exception.InvalidTokenException;
import com.accountshield.repository.RefreshTokenRepository;
import com.accountshield.security.jwt.JwtProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenServiceImpl
        implements RefreshTokenService {

    private final RefreshTokenRepository repository;

    private final JwtProperties jwtProperties;

    @Override
    public RefreshTokenEntity create(UserEntity user) {

        repository.deleteByUser(user);

        RefreshTokenEntity refreshToken = RefreshTokenEntity.builder()
                .token(UUID.randomUUID().toString())
                .user(user)
                .expiresAt(
                        LocalDateTime.now()
                                .plusSeconds(jwtProperties.getRefreshExpiration())
                )
                .revoked(false)
                .build();

        return repository.save(refreshToken);
    }

    @Override
    @Transactional(readOnly = true)
    public RefreshTokenEntity verify(String token) {

        RefreshTokenEntity refreshToken = repository.findByToken(token)
                .orElseThrow(
                        () -> new InvalidTokenException("Invalid token")
                );

        if (refreshToken.isRevoked()) {
            throw new InvalidTokenException("Token is revoked");
        }

        if (refreshToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new InvalidTokenException("Token is expired");
        }

        return refreshToken;
    }

    @Override
    public RefreshTokenEntity rotate(RefreshTokenEntity token) {

        UserEntity user = token.getUser();

        revoke(token);

        return create(user);
    }

    @Override
    public void revoke(RefreshTokenEntity token) {

        token.setRevoked(true);

        repository.save(token);
    }

    @Override
    public TokenResponse refresh(String refreshToken) {
        return null;
    }

    @Override
    public void revokeAll(UserEntity user) {

        repository.deleteByUser(user);

    }

}
