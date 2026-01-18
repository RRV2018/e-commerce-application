package com.omsoft.retail.order.controller;

import com.omsoft.retail.order.dto.CreateOrderRequest;
import com.omsoft.retail.order.dto.OrderResponse;
import com.omsoft.retail.order.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @PostMapping
    public OrderResponse placeOrder(
            @RequestBody @Valid CreateOrderRequest dto,
            @RequestHeader("X-User-Id") String userId) {

        return service.placeOrder(dto, userId);
    }

    @PostMapping("/book")
    public ResponseEntity<Void> bookOrderFromCard(@RequestHeader("X-User-Id") String userId) {
        boolean booked = service.bookOrderFromCard(userId);
        if (booked) {
            return ResponseEntity.ok().build(); // 204 No Content
        } else {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
    }


    @GetMapping
    public List<OrderResponse> getOrders(
            @RequestHeader("X-User-Id") String userId) {
        return service.getOrders(userId);
    }

    @GetMapping("/{id}")
    public Optional<OrderResponse> getOrders(@RequestHeader("X-User-Id") String userId, @PathVariable Long orderId) {
        return service.getOrder(userId, orderId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long id) {
        boolean deleted = service.cancelOrderById(id);
        if (deleted) {
            return ResponseEntity.noContent().build(); // 204 No Content
        } else {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
    }
}
