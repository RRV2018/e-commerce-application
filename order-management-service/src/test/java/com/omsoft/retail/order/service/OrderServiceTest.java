package com.omsoft.retail.order.service;

import com.omsoft.retail.order.dto.OrderResponse;
import com.omsoft.retail.order.repo.OrderRepository;
import com.omsoft.retail.order.repo.ShippingOptionRepository;
import com.omsoft.retail.order.repo.UserCardRepository;
import com.omsoft.retail.order.client.InventoryClient;
import com.omsoft.retail.order.client.PaymentClient;
import com.omsoft.retail.order.service.CouponService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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

        List<OrderResponse> result = orderService.getOrders("user1");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getOrderById_whenNotFound_returnsEmpty() {
        when(orderRepository.findByUserId("user1")).thenReturn(Collections.emptyList());

        Optional<OrderResponse> result = orderService.getOrder("user1", 1L);

        assertTrue(result.isEmpty());
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
