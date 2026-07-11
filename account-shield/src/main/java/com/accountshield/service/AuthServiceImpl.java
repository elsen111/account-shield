package com.accountshield.service;

import com.accountshield.dto.auth.AuthResponse;
import com.accountshield.dto.auth.LoginRequest;
import com.accountshield.dto.auth.RefreshTokenRequest;
import com.accountshield.dto.auth.RegisterRequest;
import com.accountshield.dto.common.UserResponse;
import com.accountshield.entity.RefreshTokenEntity;
import com.accountshield.entity.UserEntity;
import com.accountshield.enums.Role;
import com.accountshield.enums.UserStatus;
import com.accountshield.exception.EmailExistsException;
import com.accountshield.exception.UserNotFoundException;
import com.accountshield.mapper.UserMapper;
import com.accountshield.repository.UserRepository;
import com.accountshield.security.custom.CustomUserDetails;
import com.accountshield.security.jwt.JwtProperties;
import com.accountshield.security.jwt.JwtService;
import com.accountshield.security.utils.SecurityUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    private final RefreshTokenService refreshTokenService;

    private final JwtService jwtService;
    private final JwtProperties  jwtProperties;

    @Override
    @Transactional
    public UserResponse register(RegisterRequest request) {

        if(userRepository.existsByEmail(request.getEmail())) {
            throw new EmailExistsException();
        }

        UserEntity user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);
        user.setStatus(UserStatus.ACTIVE);
        userRepository.saveAndFlush(user);

        return userMapper.toResponse(user);

    }

    @Override
    @Transactional
    public AuthResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(
                        () -> new UserNotFoundException("User not found with email: " + request.getEmail())
                );

        String accessToken = jwtService.generateAccessToken(
                new CustomUserDetails(user)
        );

        RefreshTokenEntity refreshToken =
                refreshTokenService.create(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .expiresIn(jwtProperties.getAccessExpiration())
                .build();

    }

    @Override
    public AuthResponse refresh(
            RefreshTokenRequest request
    ) {

        RefreshTokenEntity refreshToken =
                refreshTokenService.verify(
                        request.refreshToken()
                );

        UserEntity user = refreshToken.getUser();

        RefreshTokenEntity newRefresh =
                refreshTokenService.rotate(refreshToken);

        String accessToken =
                jwtService.generateAccessToken(
                        new CustomUserDetails(user)
                );

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(newRefresh.getToken())
                .expiresIn(jwtProperties.getAccessExpiration())
                .build();

    }

    @Override
    public void logout() {

        UserEntity user = userRepository.findById(
                SecurityUtils.getCurrentUserId()
        ).orElseThrow(
                () -> new UserNotFoundException("User not found with id: " + SecurityUtils.getCurrentUserId())
        );

        refreshTokenService.revokeAll(user);

    }
}
