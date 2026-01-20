package com.omsoft.retail.user.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "User Management Service",
                version = "v1",
                description = "APIs for User Management Service"
        )
)
public class OpenApiConfig {
}
