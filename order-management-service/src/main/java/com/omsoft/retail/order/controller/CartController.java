package com.omsoft.retail.order.controller;

import com.omsoft.retail.order.dto.AddToCartRequest;
import com.omsoft.retail.order.dto.CartResponse;
import com.omsoft.retail.order.service.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Cart APIs", description = "Shopping cart: add, update, remove items")
@RestController
@RequestMapping("/api/order/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @Operation(summary = "Get cart", description = "Returns the current user's cart with items and totals")
    @GetMapping
    public CartResponse getCart(@RequestHeader("X-User-Id") String userId) {
        return cartService.getCart(userId);
    }

    @Operation(summary = "Add to cart")
    @PostMapping("/items")
    public CartResponse addItem(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody @Valid AddToCartRequest request) {
        return cartService.addItem(userId, request);
    }

    @Operation(summary = "Update item quantity")
    @PutMapping("/items/{productId}")
    public CartResponse updateQuantity(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable Long productId,
            @RequestParam int quantity) {
        return cartService.updateQuantity(userId, productId, quantity);
    }

    @Operation(summary = "Remove item from cart")
    @DeleteMapping("/items/{productId}")
    public CartResponse removeItem(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable Long productId) {
        return cartService.removeItem(userId, productId);
    }

    @Operation(summary = "Clear cart")
    @DeleteMapping
    public ResponseEntity<Void> clearCart(@RequestHeader("X-User-Id") String userId) {
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }
}
