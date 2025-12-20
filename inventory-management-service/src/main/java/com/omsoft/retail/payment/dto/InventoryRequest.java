package com.omsoft.retail.payment.dto;

public record InventoryRequest(
        Long productId,
        Integer quantity
) {}
