package com.omsoft.retail.payment.controller;

import com.omsoft.retail.payment.dto.PaymentRequest;
import com.omsoft.retail.payment.dto.PaymentResponse;
import com.omsoft.retail.payment.type.PaymentStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @PostMapping
    public PaymentResponse pay(@RequestBody PaymentRequest request) {
        boolean success = request.amount()
                .compareTo(BigDecimal.valueOf(100000)) < 0;

        if (success) {
            return new PaymentResponse(
                    PaymentStatus.SUCCESS,
                    UUID.randomUUID().toString()
            );
        }

        return new PaymentResponse(
                PaymentStatus.FAILED,
                null
        );
    }
}
