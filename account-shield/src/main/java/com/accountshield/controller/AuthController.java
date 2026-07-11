package com.accountshield.controller;

import com.accountshield.common.ApiResponse;
import com.accountshield.dto.auth.LoginRequest;
import com.accountshield.dto.auth.RefreshTokenRequest;
import com.accountshield.dto.auth.TokenResponse;
import com.accountshield.dto.auth.RegisterRequest;
import com.accountshield.dto.common.UserResponse;
import com.accountshield.service.AuthService;
import com.accountshield.service.RefreshTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(
        name = "Authentication",
        description = "Registration and Login APIs"
)
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;

    @Operation(
            summary = "Register new user",
            description = "Creates a new account."
    )
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(
            @Valid @RequestBody RegisterRequest request
    ) {

        UserResponse response = authService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("User registered successfully", response));

    }


    @Operation(
            summary = "Authenticate user",
            description = "Authenticates a user and returns a JWT access token."
    )
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> register(
            @Valid @RequestBody LoginRequest request
    ) {

        TokenResponse response = authService.login(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("User logged in successfully", response));

    }

    @PostMapping("/refresh")
    public TokenResponse refresh(
            @RequestBody
            RefreshTokenRequest request
    ) {

        return refreshTokenService.refresh(
                request.refreshToken()
        );

    }


}
