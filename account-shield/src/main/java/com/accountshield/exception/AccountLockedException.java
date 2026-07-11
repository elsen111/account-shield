package com.accountshield.exception;

public class AccountLockedException extends RuntimeException {
    public AccountLockedException(String message) {
        super("Account is locked currently. Try later.");
    }
}
