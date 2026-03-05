package com.omsoft.retail.product.service;

import com.omsoft.retail.product.dto.ProductResponse;
import com.omsoft.retail.product.dto.WishlistItemResponse;
import com.omsoft.retail.product.entity.Product;
import com.omsoft.retail.product.entity.WishlistItem;
import com.omsoft.retail.product.mapper.ProductMapper;
import com.omsoft.retail.product.repo.ProductRepository;
import com.omsoft.retail.product.repo.WishlistRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Transactional(readOnly = true)
    public List<WishlistItemResponse> getWishlist(String userId) {
        return wishlistRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public WishlistItemResponse addToWishlist(String userId, Long productId) {
        if (wishlistRepository.existsByUserIdAndProductId(userId, productId)) {
            return toResponse(wishlistRepository.findByUserIdAndProductId(userId, productId).orElseThrow());
        }
        productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found"));
        WishlistItem item = new WishlistItem();
        item.setUserId(userId);
        item.setProductId(productId);
        item = wishlistRepository.save(item);
        return toResponse(item);
    }

    @Transactional
    public void removeFromWishlist(String userId, Long productId) {
        wishlistRepository.findByUserIdAndProductId(userId, productId)
                .ifPresent(wishlistRepository::delete);
    }

    @Transactional(readOnly = true)
    public boolean isInWishlist(String userId, Long productId) {
        return wishlistRepository.existsByUserIdAndProductId(userId, productId);
    }

    private WishlistItemResponse toResponse(WishlistItem item) {
        Product product = productRepository.findById(item.getProductId()).orElse(null);
        ProductResponse productResponse = product != null ? productMapper.toDto(product) : null;
        return new WishlistItemResponse(
                item.getId(),
                item.getProductId(),
                productResponse,
                item.getCreatedAt()
        );
    }
}
