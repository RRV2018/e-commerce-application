package com.omsoft.retail.order.dto;

import com.omsoft.retail.order.type.OrderStatus;

import java.math.BigDecimal;
import java.util.List;

public record OrderResponse(
        Long orderId,
        String userId,
        OrderStatus status,
        BigDecimal totalAmount,
        List<OrderItemResponse> items
) {}

