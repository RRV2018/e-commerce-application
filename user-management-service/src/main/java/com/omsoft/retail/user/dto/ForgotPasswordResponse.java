package com.omsoft.retail.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ForgotPasswordResponse {

    private String message;
    /** Only set in dev when return-link-in-response is enabled; never in production. */
    private String resetLink;
}
