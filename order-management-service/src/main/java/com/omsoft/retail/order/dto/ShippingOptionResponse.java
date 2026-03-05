package com.omsoft.retail.order.dto;

import java.math.BigDecimal;

public record ShippingOptionResponse(
        Long id,
        String name,
        BigDecimal cost,
        Integer estimatedDays,
        boolean isDefault
) {}
