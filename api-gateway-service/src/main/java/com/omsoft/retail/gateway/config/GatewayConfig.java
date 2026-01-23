package com.omsoft.retail.gateway.config;

import com.omsoft.retail.gateway.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class GatewayConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public RouteLocator customRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("user-service", r -> r.path("/api/user/**", "/api/auth/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://user-management-service"))
                .route("user-swagger", r -> r.path("/api/auth/login","/api/user/v3/api-docs/**", "/api/user/swagger-ui/**")
                        .filters(f ->  f.stripPrefix(2).filter(jwtAuthenticationFilter))
                        .uri("lb://user-management-service"))
                .route("product-service", r -> r.path("/api/products/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://product-catalog-service"))
                .route("product-swagger", r -> r.path("/api/products/v3/api-docs/**", "/api/products/swagger-ui/**")
                        .filters(f ->  f.filter(jwtAuthenticationFilter))
                        .uri("lb://product-catalog-service"))
                .route("payment-service", r -> r.path("/api/payment/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://payment-management-service"))
                .route("inventory-service", r -> r.path("/api/inventory/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://inventory-management-service"))
                .route("order-service", r -> r.path("/api/order/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter))
                        .uri("lb://order-management-service"))
                .build();
        /*.route("product-swagger", r -> r.path("/api/products/v3/api-docs")
                .uri("lb://product-catalog-service"))
                                .route("payment-swagger", r -> r.path("/api/payment/v3/api-docs")
                        .uri("lb://payment-management-service"))
                        .route("inventory-swagger", r -> r.path("/api/inventory/v3/api-docs")
                        .uri("lb://inventory-management-service"))
                .route("order-swagger", r -> r.path("/api/order/v3/api-docs")
                        .uri("lb://order-management-service"))



*/
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOriginPatterns("*")
                        .allowedMethods("*")
                        .allowCredentials(true)
                        .allowedHeaders("*");
            }
        };
    }

}
