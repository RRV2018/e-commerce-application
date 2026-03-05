package com.omsoft.retail.order.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AddToCartRequest(
        @NotNull Long productId,
        @NotNull @Positive Integer quantity
) {}
