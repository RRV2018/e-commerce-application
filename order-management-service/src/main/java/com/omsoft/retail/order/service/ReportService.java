package com.omsoft.retail.order.service;

import com.omsoft.retail.order.dto.ReportSummaryResponse;
import com.omsoft.retail.order.entity.Order;
import com.omsoft.retail.order.repo.OrderRepository;
import com.omsoft.retail.order.type.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final OrderRepository orderRepository;

    public ReportSummaryResponse getSummary() {
        List<Order> all = orderRepository.findAll();
        long totalOrders = all.size();
        BigDecimal totalRevenue = all.stream()
                .filter(o -> o.getStatus() == OrderStatus.PAID || o.getStatus() == OrderStatus.SHIPPED || o.getStatus() == OrderStatus.DELIVERED)
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        Map<OrderStatus, Long> byStatus = new EnumMap<>(OrderStatus.class);
        for (OrderStatus s : OrderStatus.values()) {
            byStatus.put(s, all.stream().filter(o -> o.getStatus() == s).count());
        }
        return new ReportSummaryResponse(totalOrders, totalRevenue, byStatus);
    }
}
