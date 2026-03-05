package com.omsoft.retail.order.dto;

import java.math.BigDecimal;
import java.util.List;

public record CartResponse(
        Long cartId,
        String userId,
        List<CartItemResponse> items,
        int itemCount,
        BigDecimal totalAmount
) {}
