package com.accountshield.security.utils;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.mail")
public class AppMailProperties {

    private String from = "no-reply@accountshield.com";

}