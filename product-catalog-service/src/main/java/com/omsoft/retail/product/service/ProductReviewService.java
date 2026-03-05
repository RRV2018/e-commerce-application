package com.omsoft.retail.product.service;

import com.omsoft.retail.product.dto.ProductReviewRequest;
import com.omsoft.retail.product.dto.ProductReviewResponse;
import com.omsoft.retail.product.entity.ProductReview;
import com.omsoft.retail.product.repo.ProductReviewRepository;
import com.omsoft.retail.product.repo.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductReviewService {

    private final ProductReviewRepository reviewRepository;
    private final ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<ProductReviewResponse> getReviewsForProduct(Long productId) {
        return reviewRepository.findByProductIdOrderByCreatedAtDesc(productId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Double getAverageRating(Long productId) {
        Double avg = reviewRepository.getAverageRatingByProductId(productId);
        return avg != null ? Math.round(avg * 10.0) / 10.0 : null;
    }

    @Transactional
    public ProductReviewResponse addOrUpdateReview(String userId, Long productId, ProductReviewRequest request) {
        productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
        ProductReview review = reviewRepository.findByUserIdAndProductId(userId, productId)
                .orElse(new ProductReview());
        review.setProductId(productId);
        review.setUserId(userId);
        review.setRating(request.rating());
        review.setComment(request.comment());
        review = reviewRepository.save(review);
        return toResponse(review);
    }

    @Transactional
    public void deleteReview(String userId, Long productId) {
        reviewRepository.findByUserIdAndProductId(userId, productId)
                .ifPresent(reviewRepository::delete);
    }

    private ProductReviewResponse toResponse(ProductReview r) {
        return new ProductReviewResponse(
                r.getId(),
                r.getProductId(),
                r.getUserId(),
                r.getRating(),
                r.getComment(),
                r.getCreatedAt()
        );
    }
}
