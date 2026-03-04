package com.omsoft.retail.user.controller;

import com.omsoft.retail.user.dto.LoginRequest;
import com.omsoft.retail.user.dto.LoginResponse;
import com.omsoft.retail.user.entiry.User;
import com.omsoft.retail.user.entiry.type.Role;
import com.omsoft.retail.user.repository.UserRepository;
import com.omsoft.retail.user.service.ForgotPasswordService;
import com.omsoft.retail.user.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private ForgotPasswordService forgotPasswordService;

    private AuthController authController;

    @BeforeEach
    void setUp() {
        authController = new AuthController(jwtUtil, authenticationManager, userRepository, forgotPasswordService);
    }

    @Test
    void login_returnsOkWithToken() {
        LoginRequest request = new LoginRequest();
        request.setEmail("user@example.com");
        request.setPassword("pass");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getName()).thenReturn("user@example.com");
        User user = User.builder().email("user@example.com").role(Role.CUSTOMER).build();
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(jwtUtil.generateToken(eq("user@example.com"), eq("CUSTOMER"))).thenReturn("jwt-token");

        ResponseEntity<LoginResponse> response = authController.login(request);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertEquals("jwt-token", response.getBody().getToken());
    }
}
