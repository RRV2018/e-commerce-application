package com.omsoft.retail.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AddressResponse {
    private Long id;
    private String addressLine1;
    private String city;
    private String state;
    private String country;
    private boolean isDefault;
}
