package com.accountshield.exception;

public class RefreshTokenExpiredException extends RuntimeException {
    public RefreshTokenExpiredException(String message) {
        super("Refresh token expired.");
    }
}
