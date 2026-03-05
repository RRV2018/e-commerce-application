package com.omsoft.retail.product.dto;

import java.time.LocalDateTime;

public record WishlistItemResponse(
        Long id,
        Long productId,
        ProductResponse product,
        LocalDateTime addedAt
) {}
