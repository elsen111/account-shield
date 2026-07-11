package com.accountshield;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class AccountShieldApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccountShieldApplication.class, args);
    }

}
