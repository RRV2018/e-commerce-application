package org.omsoft.retail.exception.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorResponse {
    private String service;
    private String errorCode;
    private String message;
    private int httpStatus;
    private String path;
    private Instant timestamp;
}
