package com.omsoft.retail.order.controller;

import com.omsoft.retail.order.dto.CreateOrderRequest;
import com.omsoft.retail.order.dto.OrderResponse;
import com.omsoft.retail.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Tag(
        name = "Order APIs",
        description = "APIs for placing, booking, retrieving and cancelling orders"
)
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    // ===================== PLACE ORDER =====================
    @Operation(
            summary = "Place a new order",
            description = "Creates a new order for the given user",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Order placed successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = OrderResponse.class)
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "Invalid request payload")
            }
    )
    @PostMapping
    public OrderResponse placeOrder(
            @RequestBody @Valid CreateOrderRequest dto,
            @RequestHeader("X-User-Id") String userId) {

        return service.placeOrder(dto, userId);
    }

    // ===================== BOOK ORDER =====================
    @Operation(
            summary = "Book order from cart",
            description = "Books all items currently present in the user's cart",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Order booked successfully"),
                    @ApiResponse(responseCode = "404", description = "No cart found for user")
            }
    )
    @PostMapping("/card")
    public ResponseEntity<Void> bookOrderFromCard(@RequestHeader("X-User-Id") String userId) {
        boolean booked = service.bookOrderFromCard(userId);
        return booked
                ? ResponseEntity.ok().build()
                : ResponseEntity.notFound().build();
    }


    // ===================== GET ALL ORDERS =====================
    @Operation(
            summary = "Get all orders for a user",
            description = "Returns all orders placed by the given user",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Orders fetched successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(
                                            schema = @Schema(implementation = OrderResponse.class)
                                    )
                            )
                    )
            }
    )
    @GetMapping
    public List<OrderResponse> getOrders(
            @RequestHeader("X-User-Id") String userId) {
        return service.getOrders(userId);
    }

    // ===================== GET ORDER BY ID =====================
    @Operation(
            summary = "Get order by ID",
            description = "Returns order details for the given order ID",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Order found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = OrderResponse.class)
                            )
                    ),
                    @ApiResponse(responseCode = "404", description = "Order not found")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable("id") Long orderId) {

        return service.getOrder(userId, orderId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ===================== CANCEL ORDER =====================
    @Operation(
            summary = "Cancel order",
            description = "Cancels an order using order ID",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Order cancelled successfully"),
                    @ApiResponse(responseCode = "404", description = "Order not found")
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long id) {
        boolean deleted = service.cancelOrderById(id);
        return deleted
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }
}
