package com.omsoft.retail.inventory.controller;

import com.omsoft.retail.inventory.dto.PaymentRequest;
import com.omsoft.retail.inventory.dto.PaymentResponse;
import com.omsoft.retail.inventory.type.PaymentStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.UUID;

@Tag(
        name = "Payment APIs",
        description = "APIs for processing payments"
)
@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Operation(
            summary = "Process payment",
            description = "Processes a payment request and returns payment status",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Payment processed successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PaymentResponse.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid payment request"
                    )
            }
    )
    @PostMapping
    public PaymentResponse pay(@RequestBody PaymentRequest request) {
        boolean success = request.amount()
                .compareTo(BigDecimal.valueOf(1000000)) < 0;
        if (success) {
            return new PaymentResponse(
                    PaymentStatus.SUCCESS,
                    UUID.randomUUID().toString()
            );
        }
        return new PaymentResponse(
                PaymentStatus.FAILED,
                null
        );
    }
}
