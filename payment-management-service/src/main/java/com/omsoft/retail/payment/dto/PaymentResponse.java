package com.omsoft.retail.payment.dto;

import com.omsoft.retail.payment.type.PaymentStatus;

public record PaymentResponse(
        PaymentStatus status,
        String transactionId
) {}
