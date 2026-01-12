package com.omsoft.retail.product.dto;

import jakarta.validation.constraints.NotBlank;

public record CategoryRequest(@NotBlank
                              String name) {
}
