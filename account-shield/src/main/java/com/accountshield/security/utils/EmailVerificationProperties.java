package com.accountshield.security.utils;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "security.verification")
public class EmailVerificationProperties {
    private long tokenExpirationMinutes = 1440;

    private String baseUrl = "http://localhost:8080/api/auth/verify-email";
}