package com.accountshield.service.impl;

import com.accountshield.dto.auth.*;
import com.accountshield.dto.common.UserResponse;
import com.accountshield.entity.RefreshTokenEntity;
import com.accountshield.entity.UserEntity;
import com.accountshield.enums.Role;
import com.accountshield.enums.UserStatus;
import com.accountshield.exception.*;
import com.accountshield.mapper.UserMapper;
import com.accountshield.repository.UserRepository;
import com.accountshield.security.custom.CustomUserDetails;
import com.accountshield.security.jwt.JwtProperties;
import com.accountshield.security.jwt.JwtService;
import com.accountshield.security.utils.EmailVerificationProperties;
import com.accountshield.security.utils.SecurityUtils;
import com.accountshield.service.AuthService;
import com.accountshield.service.EmailService;
import com.accountshield.service.LoginAttemptService;
import com.accountshield.service.RefreshTokenService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

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

    private final LoginAttemptService loginAttemptService;

    private final EmailService emailService;
    private final EmailVerificationProperties verificationProperties;

    @Override
    @Transactional
    public UserResponse register(RegisterRequest request) {

        if(userRepository.existsByEmail(request.getEmail())) {
            throw new EmailExistsException();
        }

        UserEntity user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);
        user.setStatus(UserStatus.PENDING_VERIFICATION);
        user.setEmailVerified(false);
        user.setVerificationToken(UUID.randomUUID().toString());
        user.setVerificationTokenExpiry(
                LocalDateTime.now().plusMinutes(verificationProperties.getTokenExpirationMinutes())
        );

        userRepository.saveAndFlush(user);

        emailService.sendVerificationEmail(user.getEmail(), user.getVerificationToken());

        return userMapper.toResponse(user);
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(
                        () ->  new UserNotFoundException("User not found with email: " + request.getEmail())
                );

        if (!user.isEmailVerified()) {
            throw new EmailNotVerifiedException("Please verify your email before logging in.");
        }

        loginAttemptService.validateLogin(user);

        try {

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

        } catch (AuthenticationException ex) {

            loginAttemptService.loginFailed(user);

            throw ex;
        }

        loginAttemptService.loginSucceeded(user);

        String accessToken =
                jwtService.generateAccessToken(
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

    @Override
    @Transactional
    public void verifyEmail(VerifyEmailRequest request) {

        UserEntity user = userRepository.findByVerificationToken(request.token())
                .orElseThrow(
                        () -> new InvalidVerificationTokenException("Verification token is invalid.")
                );

        if (user.isEmailVerified()) {
            throw new BadRequestException("Email is already verified.");
        }

        if (user.getVerificationTokenExpiry() == null
                || user.getVerificationTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new InvalidVerificationTokenException("Verification token has expired.");
        }

        user.setEmailVerified(true);
        user.setStatus(UserStatus.ACTIVE);
        user.setVerificationToken(null);
        user.setVerificationTokenExpiry(null);

        userRepository.save(user);
    }

    @Override
    @Transactional
    public void resendVerification(ResendVerificationRequest request) {

        UserEntity user = userRepository.findByEmail(request.email())
                .orElseThrow(
                        () -> new UserNotFoundException("User not found with email: " + request.email())
                );

        if (user.isEmailVerified()) {
            throw new BadRequestException("Email is already verified.");
        }

        user.setVerificationToken(UUID.randomUUID().toString());
        user.setVerificationTokenExpiry(
                LocalDateTime.now().plusMinutes(verificationProperties.getTokenExpirationMinutes())
        );

        userRepository.save(user);

        emailService.sendVerificationEmail(user.getEmail(), user.getVerificationToken());
    }
}
