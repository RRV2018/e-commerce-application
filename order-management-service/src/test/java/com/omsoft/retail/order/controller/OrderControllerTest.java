package com.omsoft.retail.order.controller;

import com.omsoft.retail.order.dto.CreateOrderRequest;
import com.omsoft.retail.order.dto.OrderItemRequest;
import com.omsoft.retail.order.dto.OrderResponse;
import com.omsoft.retail.order.service.CouponService;
import com.omsoft.retail.order.service.OrderService;
import com.omsoft.retail.order.service.ShippingOptionService;
import com.omsoft.retail.order.type.OrderStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @Mock
    private ShippingOptionService shippingOptionService;

    @Mock
    private CouponService couponService;

    private OrderController orderController;

    @BeforeEach
    void setUp() {
        orderController = new OrderController(orderService, shippingOptionService, couponService);
    }

    @Test
    void getOrders_returnsListFromService() {
        List<OrderResponse> list = Collections.emptyList();
        when(orderService.getOrders("user1")).thenReturn(list);

        List<OrderResponse> result = orderController.getOrders("user1");

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getOrderById_whenFound_returnsOk() {
        OrderResponse order = new OrderResponse(
                "ORD00001", "user1", OrderStatus.CREATED,
                BigDecimal.valueOf(100), Collections.emptyList(), null, null
        );
        when(orderService.getOrder("user1", 1L)).thenReturn(Optional.of(order));

        ResponseEntity<OrderResponse> response = orderController.getOrderById("user1", 1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("ORD00001", response.getBody().orderId());
    }

    @Test
    void getOrderById_whenNotFound_returns404() {
        when(orderService.getOrder("user1", 999L)).thenReturn(Optional.empty());

        ResponseEntity<OrderResponse> response = orderController.getOrderById("user1", 999L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void bookOrderFromCard_whenSuccess_returnsOk() {
        when(orderService.bookOrderFromCard("user1")).thenReturn(true);

        ResponseEntity<Void> response = orderController.bookOrderFromCard("user1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void bookOrderFromCard_whenFail_returnsNotFound() {
        when(orderService.bookOrderFromCard("user1")).thenReturn(false);

        ResponseEntity<Void> response = orderController.bookOrderFromCard("user1");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void cancelOrder_whenExists_returnsNoContent() {
        when(orderService.cancelOrderById(1L)).thenReturn(true);

        ResponseEntity<Void> response = orderController.cancelOrder(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void cancelOrder_whenNotExists_returnsNotFound() {
        when(orderService.cancelOrderById(999L)).thenReturn(false);

        ResponseEntity<Void> response = orderController.cancelOrder(999L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
