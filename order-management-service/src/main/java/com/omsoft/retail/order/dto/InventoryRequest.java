package com.omsoft.retail.order.dto;

public record InventoryRequest(
        Long productId,
        Integer quantity
) {}
