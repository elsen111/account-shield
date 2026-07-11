package com.accountshield.config;

import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.*;
import io.swagger.v3.oas.models.security.*;
import org.springframework.context.annotation.*;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI accountShieldOpenApi() {

        final String securitySchemeName = "Bearer Authentication";

        return new OpenAPI()

                .info(
                        new Info()
                                .title("AccountShield API")
                                .description("""
                                        Secure User Account Management API
                                        
                                        Features:
                                        - JWT Authentication
                                        - User Profile Management
                                        - Admin User Management
                                        """)
                                .version("1.0.0")
                                .contact(
                                        new Contact()
                                                .name("AccountShield Team")
                                                .email("support@accountshield.com")
                                )
                )

                .addSecurityItem(
                        new SecurityRequirement()
                                .addList(securitySchemeName)
                )

                .components(
                        new Components()
                                .addSecuritySchemes(
                                        securitySchemeName,
                                        new SecurityScheme()

                                                .name("Authorization")

                                                .type(SecurityScheme.Type.HTTP)

                                                .scheme("bearer")

                                                .bearerFormat("JWT")
                                )
                );

    }

}