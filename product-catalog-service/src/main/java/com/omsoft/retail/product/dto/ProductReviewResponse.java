package com.omsoft.retail.product.dto;

import java.time.LocalDateTime;

public record ProductReviewResponse(
        Long id,
        Long productId,
        String userId,
        Integer rating,
        String comment,
        LocalDateTime createdAt
) {}
