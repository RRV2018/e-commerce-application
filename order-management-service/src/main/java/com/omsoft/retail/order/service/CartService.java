package com.omsoft.retail.order.service;

import com.omsoft.retail.order.client.ProductClient;
import com.omsoft.retail.order.dto.*;
import com.omsoft.retail.order.entity.Cart;
import com.omsoft.retail.order.entity.CartItem;
import com.omsoft.retail.order.repo.CartItemRepository;
import com.omsoft.retail.order.repo.CartRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductClient productClient;

    @Transactional(readOnly = true)
    public CartResponse getCart(String userId) {
        Cart cart = getOrCreateCart(userId);
        return mapToResponse(cart);
    }

    @Transactional
    public CartResponse addItem(String userId, AddToCartRequest request) {
        Cart cart = getOrCreateCart(userId);
        Optional<CartItem> existing = cartItemRepository.findByCartIdAndProductId(cart.getId(), request.productId());
        if (existing.isPresent()) {
            CartItem item = existing.get();
            item.setQuantity(item.getQuantity() + request.quantity());
        } else {
            CartItem item = new CartItem();
            item.setCart(cart);
            item.setProductId(request.productId());
            item.setQuantity(request.quantity());
            cart.getItems().add(item);
            cartRepository.save(cart);
        }
        return mapToResponse(cartRepository.findById(cart.getId()).orElseThrow());
    }

    @Transactional
    public CartResponse updateQuantity(String userId, Long productId, int quantity) {
        if (quantity <= 0) {
            return removeItem(userId, productId);
        }
        Cart cart = getOrCreateCart(userId);
        Optional<CartItem> existing = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId);
        if (existing.isPresent()) {
            existing.get().setQuantity(quantity);
            cartRepository.flush();
        }
        return mapToResponse(cartRepository.findById(cart.getId()).orElseThrow());
    }

    @Transactional
    public CartResponse removeItem(String userId, Long productId) {
        Cart cart = getOrCreateCart(userId);
        cart.getItems().removeIf(item -> item.getProductId().equals(productId));
        cartRepository.flush();
        return mapToResponse(cartRepository.findById(cart.getId()).orElseThrow());
    }

    @Transactional
    public void clearCart(String userId) {
        cartRepository.findByUserId(userId).ifPresent(cart -> {
            cart.getItems().clear();
            cartRepository.save(cart);
        });
    }

    private Cart getOrCreateCart(String userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart c = new Cart();
                    c.setUserId(userId);
                    return cartRepository.save(c);
                });
    }

    private CartResponse mapToResponse(Cart cart) {
        List<CartItemResponse> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        for (CartItem item : cart.getItems()) {
            try {
                Product p = productClient.getProduct(item.getProductId());
                BigDecimal price = p != null ? p.price() : BigDecimal.ZERO;
                BigDecimal subtotal = price.multiply(BigDecimal.valueOf(item.getQuantity()));
                total = total.add(subtotal);
                items.add(new CartItemResponse(
                        item.getId(),
                        item.getProductId(),
                        p != null ? p.name() : null,
                        price,
                        item.getQuantity(),
                        subtotal
                ));
            } catch (Exception e) {
                log.warn("Could not fetch product {} for cart: {}", item.getProductId(), e.getMessage());
                items.add(new CartItemResponse(
                        item.getId(),
                        item.getProductId(),
                        null,
                        BigDecimal.ZERO,
                        item.getQuantity(),
                        BigDecimal.ZERO
                ));
            }
        }
        return new CartResponse(
                cart.getId(),
                cart.getUserId(),
                items,
                cart.getItems().size(),
                total
        );
    }
}
