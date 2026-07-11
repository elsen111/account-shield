package com.accountshield.service.impl;

import com.accountshield.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Profile("!prod")
public class EmailServiceImpl implements EmailService {

    @Override
    public void sendVerificationEmail(String toEmail, String verificationToken) {

        String verificationLink =
                "http://localhost:8080/api/auth/verify-email?token=" + verificationToken;

        log.info(
                "[SIMULATED EMAIL] To: {} | Subject: Verify your Account Shield email | " +
                        "Body: Click the link to verify your account: {}",
                toEmail,
                verificationLink
        );

    }

}