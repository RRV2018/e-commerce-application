package com.omsoft.retail.order.client;

import com.omsoft.retail.order.dto.PaymentRequest;
import com.omsoft.retail.order.dto.PaymentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "payment-management-service",
        path = "/api/payments"
)
public interface PaymentClient {

    @PostMapping
    PaymentResponse pay(@RequestBody PaymentRequest request);
}
