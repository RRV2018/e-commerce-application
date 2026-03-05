package com.omsoft.retail.product.repo;

import com.omsoft.retail.product.entity.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WishlistRepository extends JpaRepository<WishlistItem, Long> {
    List<WishlistItem> findByUserIdOrderByCreatedAtDesc(String userId);
    Optional<WishlistItem> findByUserIdAndProductId(String userId, Long productId);
    boolean existsByUserIdAndProductId(String userId, Long productId);
}
