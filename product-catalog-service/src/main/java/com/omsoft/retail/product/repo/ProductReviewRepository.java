package com.omsoft.retail.product.repo;

import com.omsoft.retail.product.entity.ProductReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ProductReviewRepository extends JpaRepository<ProductReview, Long> {
    List<ProductReview> findByProductIdOrderByCreatedAtDesc(Long productId);
    Optional<ProductReview> findByUserIdAndProductId(String userId, Long productId);
    boolean existsByUserIdAndProductId(String userId, Long productId);

    @Query("SELECT AVG(r.rating) FROM ProductReview r WHERE r.productId = :productId")
    Double getAverageRatingByProductId(Long productId);
}
