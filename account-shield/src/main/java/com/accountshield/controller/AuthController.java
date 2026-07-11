package com.accountshield.controller;

import com.accountshield.common.ApiResponse;
import com.accountshield.dto.auth.*;
import com.accountshield.dto.common.UserResponse;
import com.accountshield.service.AuthService;
import com.accountshield.service.RefreshTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody LoginRequest request
    ) {

        AuthResponse response = authService.login(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("User logged in successfully", response));

    }

    @Operation(summary = "Refresh the token")
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(

            @Valid
            @RequestBody
            RefreshTokenRequest request
    ) {

        AuthResponse response = authService.refresh(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Token refreshed", response));

    }

    @Operation(summary = "Logout from the account")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<AuthResponse>> logout() {

        authService.logout();

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Logged out successfully", null));

    }

    @Operation(
            summary = "Verify email",
            description = "Confirms a user's email address using the verification token sent at registration."
    )
    @PostMapping("/verify-email")
    public ResponseEntity<ApiResponse<Void>> verifyEmail(
            @Valid @RequestBody VerifyEmailRequest request
    ) {
        authService.verifyEmail(request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("Email verified successfully", null));
    }

    @Operation(
            summary = "Resend verification email",
            description = "Issues a new verification token and simulates re-sending the verification email."
    )
    @PostMapping("/resend-verification")
    public ResponseEntity<ApiResponse<Void>> resendVerification(
            @Valid @RequestBody ResendVerificationRequest request
    ) {
        authService.resendVerification(request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success("Verification email resent", null));
    }

}
