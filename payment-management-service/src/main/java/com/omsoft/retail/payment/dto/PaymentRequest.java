package com.omsoft.retail.payment.dto;

import java.math.BigDecimal;

public record PaymentRequest(
        Long orderId,
        BigDecimal amount,
        String userId
) {}
