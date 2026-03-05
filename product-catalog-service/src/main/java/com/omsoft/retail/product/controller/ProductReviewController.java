package com.omsoft.retail.product.controller;

import com.omsoft.retail.product.dto.ProductReviewRequest;
import com.omsoft.retail.product.dto.ProductReviewResponse;
import com.omsoft.retail.product.service.ProductReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "Product Reviews", description = "Reviews and ratings for products")
@RestController
@RequestMapping("/api/products")
public class ProductReviewController {

    private final ProductReviewService reviewService;

    public ProductReviewController(ProductReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @Operation(summary = "Get reviews for a product")
    @GetMapping("/{productId}/reviews")
    public List<ProductReviewResponse> getReviews(@PathVariable Long productId) {
        return reviewService.getReviewsForProduct(productId);
    }

    @Operation(summary = "Get average rating for a product")
    @GetMapping("/{productId}/rating")
    public Map<String, Double> getAverageRating(@PathVariable Long productId) {
        Double avg = reviewService.getAverageRating(productId);
        return Map.of("averageRating", avg != null ? avg : 0.0);
    }

    @Operation(summary = "Add or update my review")
    @PostMapping("/{productId}/reviews")
    public ProductReviewResponse addOrUpdateReview(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable Long productId,
            @RequestBody @Valid ProductReviewRequest request) {
        return reviewService.addOrUpdateReview(userId, productId, request);
    }

    @Operation(summary = "Delete my review")
    @DeleteMapping("/{productId}/reviews")
    public ResponseEntity<Void> deleteReview(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable Long productId) {
        reviewService.deleteReview(userId, productId);
        return ResponseEntity.noContent().build();
    }
}
