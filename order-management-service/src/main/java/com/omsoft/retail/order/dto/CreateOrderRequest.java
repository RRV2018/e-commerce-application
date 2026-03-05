package com.omsoft.retail.order.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record CreateOrderRequest(
        @NotEmpty List<OrderItemRequest> items,
        String couponCode,
        Long shippingOptionId
) {
    public CreateOrderRequest(List<OrderItemRequest> items) {
        this(items, null, null);
    }
}
