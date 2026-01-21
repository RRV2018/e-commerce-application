package com.omsoft.retail.gateway.config;

import com.omsoft.retail.gateway.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class GatewayConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public RouteLocator customRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("user-service", r -> r.path("/api/users/**", "/api/auth/**")
                        .filters(f -> f.stripPrefix(1).filter(jwtAuthenticationFilter))
                        .uri("lb://user-management-service"))
                .route("user-swagger", r -> r.path("/users/v3/api-docs")
                        .filters(f -> f.stripPrefix(1))
                        .uri("lb://user-management-service"))
                .route("product-service", r -> r.path("/api/products/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://product-catalog-service"))
                .route("product-swagger", r -> r.path("/api/products/v3/api-docs")
                        .uri("lb://product-catalog-service"))
                .route("payment-service", r -> r.path("/api/payment/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://payment-management-service"))
                .route("payment-swagger", r -> r.path("/api/payment/v3/api-docs")
                        .uri("lb://payment-management-service"))
                .route("inventory-service", r -> r.path("/api/inventory/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://inventory-management-service"))
                .route("inventory-swagger", r -> r.path("/api/inventory/v3/api-docs")
                        .uri("lb://inventory-management-service"))
                .route("order-service", r -> r.path("/api/order/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://order-management-service"))
                .route("order-swagger", r -> r.path("/api/order/v3/api-docs")
                        .uri("lb://order-management-service"))
                .build();
    }
}
