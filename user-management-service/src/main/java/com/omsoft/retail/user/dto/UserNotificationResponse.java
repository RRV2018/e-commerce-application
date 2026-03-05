package com.omsoft.retail.user.dto;

import java.time.LocalDateTime;

public record UserNotificationResponse(
        Long id,
        String title,
        String message,
        boolean read,
        LocalDateTime createdAt
) {}
