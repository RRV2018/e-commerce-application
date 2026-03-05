package com.omsoft.retail.order.dto;

import com.omsoft.retail.order.type.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateOrderStatusRequest(@NotNull OrderStatus status) {}
