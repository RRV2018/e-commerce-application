package com.omsoft.retail.order.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Order Management Service",
                version = "v1",
                description = "APIs for Order Management"
        )
)
public class OpenApiConfig {
}
