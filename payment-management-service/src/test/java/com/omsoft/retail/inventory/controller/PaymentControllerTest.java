package com.omsoft.retail.inventory.controller;

import com.omsoft.retail.inventory.dto.PaymentRequest;
import com.omsoft.retail.inventory.dto.PaymentResponse;
import com.omsoft.retail.inventory.type.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    private PaymentController paymentController;

    @BeforeEach
    void setUp() {
        paymentController = new PaymentController();
    }

    @Test
    void pay_whenAmountBelowLimit_returnsSuccess() {
        PaymentRequest request = new PaymentRequest(1L, BigDecimal.valueOf(100), "user1");

        PaymentResponse response = paymentController.pay(request);

        assertNotNull(response);
        assertEquals(PaymentStatus.SUCCESS, response.status());
        assertNotNull(response.transactionId());
    }

    @Test
    void pay_whenAmountAtOrAboveLimit_returnsFailed() {
        PaymentRequest request = new PaymentRequest(
                1L,
                BigDecimal.valueOf(1000000),
                "user1"
        );

        PaymentResponse response = paymentController.pay(request);

        assertNotNull(response);
        assertEquals(PaymentStatus.FAILED, response.status());
        assertNull(response.transactionId());
    }
}
