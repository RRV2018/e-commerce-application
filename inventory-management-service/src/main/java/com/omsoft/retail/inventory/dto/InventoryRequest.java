package com.omsoft.retail.inventory.dto;

public record InventoryRequest(
        Long productId,
        Integer quantity
) {}
