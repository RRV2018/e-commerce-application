package com.omsoft.retail.payment.controller;

import com.omsoft.retail.payment.dto.InventoryRequest;
import com.omsoft.retail.payment.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@Tag(
        name = "Inventory APIs",
        description = "APIs for reserving, confirming and releasing inventory"
)
@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService service;

    public InventoryController(InventoryService service) {
        this.service = service;
    }
    // ===================== RESERVE INVENTORY =====================
    @Operation(
            summary = "Reserve inventory",
            description = "Temporarily reserves inventory for a product",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Inventory reserved successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid inventory request"),
                    @ApiResponse(responseCode = "409", description = "Insufficient inventory")
            }
    )
    @PostMapping("/reserve")
    public ResponseEntity<Void> reserve(@RequestBody InventoryRequest dto) {
        service.reserve(dto.productId(), dto.quantity());
        return ResponseEntity.noContent().build();
    }
    // ===================== CONFIRM INVENTORY =====================
    @Operation(
            summary = "Confirm inventory",
            description = "Confirms reserved inventory after successful order",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Inventory confirmed successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid inventory request"),
                    @ApiResponse(responseCode = "404", description = "Reservation not found")
            }
    )

    @PostMapping("/confirm")
    public ResponseEntity<Void> confirm(@RequestBody InventoryRequest dto) {
        service.confirm(dto.productId(), dto.quantity());
        return ResponseEntity.noContent().build();
    }

    // ===================== RELEASE INVENTORY =====================
    @Operation(
            summary = "Release inventory",
            description = "Releases previously reserved inventory",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Inventory released successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid inventory request"),
                    @ApiResponse(responseCode = "404", description = "Reservation not found")
            }
    )
    @PostMapping("/release")
    public ResponseEntity<Void> release(@RequestBody InventoryRequest dto) {
        service.release(dto.productId(), dto.quantity());
        return ResponseEntity.noContent().build();
    }
}
