package com.omsoft.retail.order.dto;

import com.omsoft.retail.order.type.PaymentStatus;

public record PaymentResponse(
        PaymentStatus status,
        String transactionId
) {}
