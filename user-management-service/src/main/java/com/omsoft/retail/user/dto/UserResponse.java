package com.omsoft.retail.user.dto;

import com.omsoft.retail.user.entiry.type.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private Role role;
    private List<AddressResponse> addresses;
}
