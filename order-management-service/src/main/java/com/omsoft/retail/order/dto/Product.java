package com.omsoft.retail.order.dto;

import java.math.BigDecimal;

public record Product(
        Long id,
        BigDecimal price,
        Integer stock
) {}
