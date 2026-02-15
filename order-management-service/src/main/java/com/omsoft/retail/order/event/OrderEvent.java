package com.omsoft.retail.order.event;

import com.omsoft.retail.order.type.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderEvent {
    private Long orderId;
    private List<ProductEvent> products;
    private OrderStatus type;
}
