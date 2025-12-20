package com.omsoft.retail.user.dto;

import com.omsoft.retail.user.entiry.type.AddressType;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddressRequest {

    @NotBlank
    private String addressLine1;

    private String addressLine2;

    @NotBlank
    private String city;

    @NotBlank
    private String state;

    @NotBlank
    private String country;

    @NotBlank
    private String postalCode;

    private AddressType addressType;
    private boolean isDefault;
}
