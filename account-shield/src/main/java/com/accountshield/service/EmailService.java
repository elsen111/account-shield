package com.accountshield.service;

public interface EmailService {
    void sendVerificationEmail(String toEmail, String verificationToken);
}
