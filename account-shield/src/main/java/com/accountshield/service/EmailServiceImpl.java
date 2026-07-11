package com.accountshield.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * Simulated email sender used for local development and testing.
 * <p>
 * No real SMTP/mail provider is used here — this logs what would have been
 * sent so the verification flow can be exercised end-to-end without needing
 * real email infrastructure or SMTP credentials.
 * <p>
 * Active for every profile except "prod" — see {@link SmtpEmailServiceImpl}
 * for the implementation that actually sends email.
 */
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