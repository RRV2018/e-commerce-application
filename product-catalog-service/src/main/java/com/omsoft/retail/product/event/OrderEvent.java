package com.omsoft.retail.product.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderEvent {
    private Long orderId;
    private List<com.omsoft.retail.product.event.ProductEvent> products;
    private OrderStatus type;
}
