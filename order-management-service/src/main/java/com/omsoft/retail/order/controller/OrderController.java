package com.omsoft.retail.order.controller;

import com.omsoft.retail.order.dto.CreateOrderRequest;
import com.omsoft.retail.order.dto.OrderResponse;
import com.omsoft.retail.order.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping
    public List<OrderResponse> getOrders(
            @RequestHeader("X-User-Id") String userId) {
        return service.getOrders(userId);
    }
}
