package com.accountshield.security.utils;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "security.login")
public class LoginSecurityProperties {

    private int maxAttempts = 5;

    private long lockDurationMinutes = 15;

}
