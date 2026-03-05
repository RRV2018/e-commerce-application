package com.omsoft.retail.user.repository;

import com.omsoft.retail.user.entiry.PasswordResetToken;

import java.time.Instant;
import java.util.Optional;

public interface PasswordResetTokenRepository extends org.springframework.data.jpa.repository.JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByTokenAndUsedFalseAndExpiresAtAfter(String token, Instant now);

    void deleteByUserId(Long userId);

    void deleteByExpiresAtBefore(Instant now);
}
