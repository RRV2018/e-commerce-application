package com.omsoft.retail.order.service;

import com.omsoft.retail.order.client.InventoryClient;
import com.omsoft.retail.order.client.PaymentClient;
import com.omsoft.retail.order.client.ProductClient;
import com.omsoft.retail.order.dto.*;
import com.omsoft.retail.order.entity.Order;
import com.omsoft.retail.order.entity.OrderItem;
import com.omsoft.retail.order.entity.UserCard;
import com.omsoft.retail.order.repo.OrderRepository;
import com.omsoft.retail.order.repo.UserCardRepository;
import com.omsoft.retail.order.type.OrderStatus;
import com.omsoft.retail.order.type.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;
    private final PaymentClient paymentClient;
    private final UserCardRepository cardRepo;

    public boolean bookOrderFromCard(String userId) {
        List<UserCard> userCardItems =  cardRepo.findUserOrders(userId);
        if (!CollectionUtils.isEmpty(userCardItems)) {
            List<OrderItemRequest> items = new ArrayList<>();
            userCardItems.forEach(item -> items.add(new OrderItemRequest(item.getProductId(), item.getQuantity().intValue(), item.getAmount())));
            CreateOrderRequest request = new CreateOrderRequest(items);
            placeOrder(request, userId);
            userCardItems.forEach(this::clearCardItem);
        }
        return true;
    }

    private void clearCardItem(UserCard item) {
        cardRepo.deleteById(item.getId());
    }

    @Transactional
    public OrderResponse placeOrder(CreateOrderRequest dto, String userId) {
        // 1️ Create Order entity
        Order order = new Order();
        order.setUserId(userId);
        order.setStatus(OrderStatus.CREATED);
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OrderItemRequest itemDto : dto.items()) {
            OrderItem item = new OrderItem();
            item.setProductId(itemDto.productId());
            item.setQuantity(itemDto.quantity());
            item.setPrice(itemDto.price());
            item.setOrder(order);
            order.getItems().add(item);
            totalAmount = totalAmount.add(
                    itemDto.price().multiply(
                            BigDecimal.valueOf(itemDto.quantity())
                    )
            );
        }
        order.setTotalAmount(totalAmount);
        // 2 Save order to generate orderId
        orderRepository.save(order);
        try {
            // 3️ Reserve inventory
            for (OrderItem item : order.getItems()) {
                inventoryClient.reserve(
                        new InventoryRequest(
                                item.getProductId(),
                                item.getQuantity()
                        )
                );
            }

            // 4️ Call payment service
            PaymentResponse paymentResponse =
                    paymentClient.pay(
                            new PaymentRequest(
                                    order.getId(),
                                    order.getTotalAmount(),
                                    userId
                            )
                    );

            if (paymentResponse.status() != PaymentStatus.SUCCESS) {
                throw new IllegalStateException("Payment failed");
            }

            // 5️ Confirm inventory
            for (OrderItem item : order.getItems()) {
                inventoryClient.confirm(
                        new InventoryRequest(
                                item.getProductId(),
                                item.getQuantity()
                        )
                );
            }
            order.setStatus(OrderStatus.PAID);
        } catch (Exception ex) {
            // 6️ Release inventory on failure
            for (OrderItem item : order.getItems()) {
                try {
                    inventoryClient.release(
                            new InventoryRequest(
                                    item.getProductId(),
                                    item.getQuantity()
                            )
                    );
                } catch (Exception ignored) {
                }
            }
            order.setStatus(OrderStatus.CANCELLED);
            throw ex;
        }
        return mapToResponse(orderRepository.save(order));
    }

    public List<OrderResponse> getOrders(String userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        return Optional.ofNullable(orders).orElse(Collections.emptyList())
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public Optional<OrderResponse> getOrder(String userId, Long orderId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        return Optional.ofNullable(orders).orElse(Collections.emptyList())
                .stream()
                .filter(order -> order.getId().equals(orderId))
                .map(this::mapToResponse)
                .findFirst();
    }

    public boolean cancelOrderById(Long id) {
        if (orderRepository.existsById(id)) {
            orderRepository.deleteById(id);
            return true;
        }
        return false;
    }

    private OrderResponse mapToResponse(Order order) {
        List<OrderItemResponse> itemResponses =
                order.getItems()
                        .stream()
                        .map(item -> new OrderItemResponse(
                                item.getProductId(),
                                item.getQuantity(),
                                item.getPrice()
                        ))
                        .toList(); // Java 17

        return new OrderResponse(
                order.getId(),
                order.getUserId(),
                order.getStatus(),
                order.getTotalAmount(),
                itemResponses
        );
    }

}

