package com.accountshield.service.impl;

import com.accountshield.security.utils.AppMailProperties;
import com.accountshield.security.utils.EmailVerificationProperties;
import com.accountshield.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@Profile("prod")
@RequiredArgsConstructor
public class SmtpEmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final AppMailProperties mailProperties;
    private final EmailVerificationProperties verificationProperties;

    @Override
    public void sendVerificationEmail(String toEmail, String verificationToken) {

        String verificationLink =
                verificationProperties.getBaseUrl() + "?token=" + verificationToken;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailProperties.getFrom());
        message.setTo(toEmail);
        message.setSubject("Verify your Account Shield email");
        message.setText(
                "Welcome to Account Shield!\n\n" +
                        "Please verify your email address by clicking the link below:\n" +
                        verificationLink + "\n\n" +
                        "This link will expire in " + verificationProperties.getTokenExpirationMinutes() +
                        " minutes. If you didn't create this account, you can ignore this email."
        );

        try {
            mailSender.send(message);
            log.info("Verification email sent to {}", toEmail);
        } catch (MailException ex) {
            log.error("Failed to send verification email to {}: {}", toEmail, ex.getMessage());
        }

    }

}