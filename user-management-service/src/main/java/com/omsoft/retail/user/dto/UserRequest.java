package com.omsoft.retail.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRequest {
    @NotBlank
    private String name;

    @Email
    @NotBlank
    private String email;

    @Size(min = 6)
    private String password;
}
