package com.omsoft.retail.order.dto;

import com.omsoft.retail.order.type.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        String orderId,
        String userId,
        OrderStatus status,
        BigDecimal totalAmount,
        BigDecimal discountAmount,
        BigDecimal shippingAmount,
        String couponCode,
        List<OrderItemResponse> items,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public OrderResponse(String orderId, String userId, OrderStatus status, BigDecimal totalAmount,
                         List<OrderItemResponse> items, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this(orderId, userId, status, totalAmount, null, null, null, items, createdAt, updatedAt);
    }
}

