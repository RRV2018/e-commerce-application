package com.omsoft.retail.order.service;

import com.omsoft.retail.order.dto.OrderResponse;
import com.omsoft.retail.order.entity.Order;
import com.omsoft.retail.order.repo.OrderRepository;
import com.omsoft.retail.order.repo.ShippingOptionRepository;
import com.omsoft.retail.order.repo.UserCardRepository;
import com.omsoft.retail.order.client.InventoryClient;
import com.omsoft.retail.order.client.PaymentClient;
import com.omsoft.retail.order.service.CouponService;
import com.omsoft.retail.order.type.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private InventoryClient inventoryClient;

    @Mock
    private PaymentClient paymentClient;

    @Mock
    private UserCardRepository cardRepo;

    @Mock
    private CouponService couponService;

    @Mock
    private ShippingOptionRepository shippingOptionRepository;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(
                orderRepository,
                inventoryClient,
                paymentClient,
                cardRepo,
                couponService,
                shippingOptionRepository,
                kafkaTemplate
        );
    }

    @Test
    void getOrders_whenEmpty_returnsEmptyList() {
        when(orderRepository.findByUserId("user1")).thenReturn(Collections.emptyList());

        List<OrderResponse> result = orderService.getOrders("user1", null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getOrders_whenAdmin_usesFindAllSorted() {
        when(orderRepository.findAll(any(Sort.class))).thenReturn(Collections.emptyList());

        List<OrderResponse> result = orderService.getOrders("admin@example.com", "ADMIN");

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(orderRepository).findAll(any(Sort.class));
        verify(orderRepository, never()).findByUserId(any());
    }

    @Test
    void getOrderById_whenNotFound_returnsEmpty() {
        when(orderRepository.findByUserId("user1")).thenReturn(Collections.emptyList());

        Optional<OrderResponse> result = orderService.getOrder("user1", 1L, null);

        assertTrue(result.isEmpty());
    }

    @Test
    void getOrder_whenAdmin_resolvesByOrderId() {
        Order order = new Order();
        order.setId(5L);
        order.setUserId("customer@example.com");
        order.setStatus(OrderStatus.CREATED);
        order.setTotalAmount(BigDecimal.TEN);
        order.setItems(new ArrayList<>());
        when(orderRepository.findById(5L)).thenReturn(Optional.of(order));

        Optional<OrderResponse> result = orderService.getOrder("admin@example.com", 5L, "ADMIN");

        assertTrue(result.isPresent());
        assertEquals("ORD00005", result.get().orderId());
        verify(orderRepository).findById(5L);
        verify(orderRepository, never()).findByUserId(any());
    }

    @Test
    void cancelOrderById_whenExists_returnsTrue() {
        when(orderRepository.existsById(1L)).thenReturn(true);

        boolean result = orderService.cancelOrderById(1L);

        assertTrue(result);
    }

    @Test
    void cancelOrderById_whenNotExists_returnsFalse() {
        when(orderRepository.existsById(999L)).thenReturn(false);

        boolean result = orderService.cancelOrderById(999L);

        assertFalse(result);
    }

    @Test
    void generateOrderNumber_formatsCorrectly() {
        String num = orderService.generateOrderNumber(1L);
        assertEquals("ORD00001", num);
        assertEquals("ORD12345", orderService.generateOrderNumber(12345L));
    }
}
