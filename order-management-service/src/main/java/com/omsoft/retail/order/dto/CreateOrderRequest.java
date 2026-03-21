package com.omsoft.retail.order.dto;

import com.omsoft.retail.order.type.PaymentMethod;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CreateOrderRequest(
        @NotEmpty List<OrderItemRequest> items,
        String couponCode,
        Long shippingOptionId,
        PaymentMethod paymentMethod
) {
    public CreateOrderRequest(List<OrderItemRequest> items) {
        this(items, null, null, null);
    }
}
