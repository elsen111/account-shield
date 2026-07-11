package com.accountshield.service;

import com.accountshield.dto.auth.LoginRequest;
import com.accountshield.dto.auth.TokenResponse;
import com.accountshield.dto.auth.RegisterRequest;
import com.accountshield.dto.common.UserResponse;
import com.accountshield.entity.UserEntity;
import com.accountshield.enums.Role;
import com.accountshield.enums.UserStatus;
import com.accountshield.exception.BadRequestException;
import com.accountshield.exception.EmailExistsException;
import com.accountshield.exception.UserNotFoundException;
import com.accountshield.mapper.UserMapper;
import com.accountshield.repository.UserRepository;
import com.accountshield.security.custom.CustomUserDetails;
import com.accountshield.security.jwt.JwtService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

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
    public TokenResponse login(LoginRequest request) {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (AuthenticationException ex) {
            throw new BadRequestException("Invalid email or password.");
        }

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(
                        () ->  new UserNotFoundException("User not found.")
                );

        CustomUserDetails userDetails =
                new CustomUserDetails(user);

        String token =
                jwtService.generateAccessToken(userDetails);

        return TokenResponse.builder()
                .accessToken(token)
                .refreshToken(null)
                .expiresIn(3600L)
                .build();
    }
}
