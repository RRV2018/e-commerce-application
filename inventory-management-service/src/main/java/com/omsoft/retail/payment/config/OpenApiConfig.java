package com.omsoft.retail.payment.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Inventory Management Service",
                version = "v1",
                description = "APIs for Inventory Management Service"
        )
)
public class OpenApiConfig {
}
