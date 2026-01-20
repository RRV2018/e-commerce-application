package com.omsoft.retail.product.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Product Management Service",
                version = "v1",
                description = "APIs for Product Catalog Management Service"
        )
)
public class OpenApiConfig {
}
