package com.omsoft.retail.product.controller;

import com.omsoft.retail.product.dto.WishlistItemResponse;
import com.omsoft.retail.product.service.WishlistService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Wishlist APIs", description = "Add, list, remove wishlist items")
@RestController
@RequestMapping("/api/products/wishlist")
public class WishlistController {

    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @Operation(summary = "Get my wishlist")
    @GetMapping
    public List<WishlistItemResponse> getWishlist(@RequestHeader("X-User-Id") String userId) {
        return wishlistService.getWishlist(userId);
    }

    @Operation(summary = "Add product to wishlist")
    @PostMapping("/{productId}")
    public WishlistItemResponse add(@RequestHeader("X-User-Id") String userId, @PathVariable Long productId) {
        return wishlistService.addToWishlist(userId, productId);
    }

    @Operation(summary = "Remove from wishlist")
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> remove(@RequestHeader("X-User-Id") String userId, @PathVariable Long productId) {
        wishlistService.removeFromWishlist(userId, productId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Check if product is in wishlist")
    @GetMapping("/check/{productId}")
    public boolean isInWishlist(@RequestHeader("X-User-Id") String userId, @PathVariable Long productId) {
        return wishlistService.isInWishlist(userId, productId);
    }
}
