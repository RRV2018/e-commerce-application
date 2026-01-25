package com.omsoft.retail.inventory.service;

import com.omsoft.retail.inventory.dto.PaymentRequest;
import com.omsoft.retail.inventory.dto.PaymentResponse;
import com.omsoft.retail.inventory.type.PaymentStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class PaymentService {

    public PaymentResponse processPayment(PaymentRequest request) {

        // Mock rule: fail if amount >= 1,00,000
        boolean success =
                request.amount().compareTo(BigDecimal.valueOf(100_000)) < 0;

        if (!success) {
            return new PaymentResponse(
                    PaymentStatus.FAILED,
                    null
            );
        }

        return new PaymentResponse(
                PaymentStatus.SUCCESS,
                UUID.randomUUID().toString()
        );
    }
}
