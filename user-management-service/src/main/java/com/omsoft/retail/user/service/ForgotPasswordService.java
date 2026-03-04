package com.omsoft.retail.user.service;

import com.omsoft.retail.user.dto.ForgotPasswordResponse;
import com.omsoft.retail.user.dto.ResetPasswordRequest;
import com.omsoft.retail.user.entiry.PasswordResetToken;
import com.omsoft.retail.user.entiry.User;
import com.omsoft.retail.user.repository.PasswordResetTokenRepository;
import com.omsoft.retail.user.repository.UserRepository;
import com.omsoft.retail.user.util.EncryptDecryptUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ForgotPasswordService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EncryptDecryptUtil encryptDecryptUtil;

    @Value("${app.password-reset.token-validity-minutes:60}")
    private int tokenValidityMinutes;

    @Value("${app.password-reset.base-url:http://localhost:3000}")
    private String baseUrl;

    @Value("${app.password-reset.return-link-in-response:true}")
    private boolean returnLinkInResponse;

    /**
     * Creates a reset token for the given email. Always returns a generic success to avoid revealing whether the email exists.
     * Optionally includes reset link in response for development (return-link-in-response=true).
     */
    @Transactional
    public ForgotPasswordResponse requestPasswordReset(String email) {
        String message = "If an account exists for this email, you will receive instructions to reset your password.";
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            log.debug("Forgot password requested for unknown email: {}", email);
            return ForgotPasswordResponse.builder().message(message).build();
        }
        User user = userOpt.get();
        tokenRepository.deleteByUserId(user.getId());
        String token = UUID.randomUUID().toString().replace("-", "");
        Instant expiresAt = Instant.now().plusSeconds(tokenValidityMinutes * 60L);
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .user(user)
                .expiresAt(expiresAt)
                .used(false)
                .createdAt(Instant.now())
                .build();
        tokenRepository.save(resetToken);
        log.info("Password reset token created for user id={}", user.getId());
        String resetLink = null;
        if (returnLinkInResponse) {
            resetLink = baseUrl + "/reset-password?token=" + token;
        }
        return ForgotPasswordResponse.builder().message(message).resetLink(resetLink).build();
    }

    /**
     * Resets password using a valid token. Throws if token invalid or expired.
     */
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        Instant now = Instant.now();
        PasswordResetToken resetToken = tokenRepository
                .findByTokenAndUsedFalseAndExpiresAtAfter(request.getToken(), now)
                .orElseThrow(() -> new IllegalArgumentException("Invalid or expired reset token"));
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setDecryptablePassword(encryptDecryptUtil.encrypt(request.getNewPassword()));
        userRepository.save(user);
        resetToken.setUsed(true);
        tokenRepository.save(resetToken);
        log.info("Password reset completed for user id={}", user.getId());
    }

    @Scheduled(cron = "${app.password-reset.cleanup-cron:0 0 * * * *}") // default: every hour
    @Transactional
    public void deleteExpiredTokens() {
        tokenRepository.deleteByExpiresAtBefore(Instant.now());
    }
}
