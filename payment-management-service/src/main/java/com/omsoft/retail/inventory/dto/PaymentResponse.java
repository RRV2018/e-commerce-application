package com.omsoft.retail.inventory.dto;

import com.omsoft.retail.inventory.type.PaymentStatus;

public record PaymentResponse(
        PaymentStatus status,
        String transactionId
) {}
