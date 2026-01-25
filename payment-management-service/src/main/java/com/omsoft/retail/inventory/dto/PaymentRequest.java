package com.omsoft.retail.inventory.dto;

import java.math.BigDecimal;

public record PaymentRequest(
        Long orderId,
        BigDecimal amount,
        String userId
) {}
