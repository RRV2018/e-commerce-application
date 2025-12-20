package com.omsoft.retail.order.dto;

import java.math.BigDecimal;

public record PaymentRequest(
        Long orderId,
        BigDecimal amount,
        String userId
) {}
