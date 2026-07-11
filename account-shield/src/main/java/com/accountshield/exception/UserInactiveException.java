package com.accountshield.exception;

public class UserInactiveException extends RuntimeException {
    public UserInactiveException(String message) {
        super("User is not active.");
    }
}
