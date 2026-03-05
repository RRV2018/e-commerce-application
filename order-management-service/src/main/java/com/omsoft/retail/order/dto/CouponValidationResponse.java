package com.omsoft.retail.order.dto;

import java.math.BigDecimal;

public record CouponValidationResponse(
        boolean valid,
        String message,
        BigDecimal discountAmount,
        String couponCode
) {}
