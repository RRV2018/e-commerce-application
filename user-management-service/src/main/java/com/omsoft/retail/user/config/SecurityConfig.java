package com.omsoft.retail.user.config;

import com.omsoft.retail.user.error.CustomAccessDeniedHandler;
import com.omsoft.retail.user.error.CustomAuthEntryPoint;
import com.omsoft.retail.user.filter.JwtFilter;
import com.omsoft.retail.user.service.impl.CustomUserDetailsService;
import com.omsoft.retail.user.util.EncryptDecryptUtil;
import com.omsoft.retail.user.util.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    @Value("${application.secret.key}")
    private String secret;


    @Bean
    public JwtFilter jwtFilter(JwtUtil jwtUtil, CustomUserDetailsService uds) {
        return new JwtFilter(jwtUtil, uds);
    }

    @Bean
    @Order(1)
    public SecurityFilterChain swaggerSecurity(HttpSecurity http) throws Exception {
        http
                .securityMatcher(
                        "/v3/api-docs/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/actuator/**",
                        "/api/user/v3/api-docs/**",
                        "/api/user/swagger-ui/**",
                        "/api/user/swagger-ui.html"
                )

                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain authEndpoints(HttpSecurity http) throws Exception {
        http
                .securityMatcher(
                        "/api/user/api/auth/**",    // gateway
                        "/api/auth/**",             // direct
                        "/api/user/api/user/register",
                        "/api/user/register"

                )
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());

        return http.build();
    }

    @Bean
    @Order(3)
    public SecurityFilterChain securedApis(HttpSecurity http,
                                           CustomAccessDeniedHandler accessDeniedHandler,
                                           CustomAuthEntryPoint authEntryPoint,
                                           JwtFilter jwtFilter) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth ->
                        auth
                        .anyRequest().authenticated()
                ).exceptionHandling(ex -> ex
                        .accessDeniedHandler(accessDeniedHandler)
                        .authenticationEntryPoint(authEntryPoint)
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
