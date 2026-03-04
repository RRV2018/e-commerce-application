package com.omsoft.retail.user.controller;

import com.omsoft.retail.user.dto.ForgotPasswordRequest;
import com.omsoft.retail.user.dto.ForgotPasswordResponse;
import com.omsoft.retail.user.dto.LoginRequest;
import com.omsoft.retail.user.dto.LoginResponse;
import com.omsoft.retail.user.dto.ResetPasswordRequest;
import com.omsoft.retail.user.entiry.User;
import com.omsoft.retail.user.service.ForgotPasswordService;
import com.omsoft.retail.user.repository.UserRepository;
import com.omsoft.retail.user.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.authentication.AuthenticationManager;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Authentication Manager APIs")
public class AuthController {

    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final ForgotPasswordService forgotPasswordService;

    public AuthController(JwtUtil jwtUtil, AuthenticationManager authenticationManager,
                          UserRepository userRepository, ForgotPasswordService forgotPasswordService) {
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.forgotPasswordService = forgotPasswordService;
    }

    @Operation(
            summary = "User login",
            description = "Authenticates user credentials and returns login response",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Login successful",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = LoginResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Invalid username or password"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid login request"
                    )
            }
    )
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
            String email = auth.getName();
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new BadCredentialsException("User not found"));
            String token = jwtUtil.generateToken(email, user.getRole().name());
            return ResponseEntity.ok(new LoginResponse(token));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @Operation(
            summary = "Forgot password",
            description = "Request a password reset link for the given email. Always returns success to avoid email enumeration."
    )
    @PostMapping("/forgot-password")
    public ResponseEntity<ForgotPasswordResponse> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        ForgotPasswordResponse response = forgotPasswordService.requestPasswordReset(request.getEmail());
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Reset password",
            description = "Reset password using the token received via email (or dev link)."
    )
    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        forgotPasswordService.resetPassword(request);
        return ResponseEntity.ok().build();
    }
}
