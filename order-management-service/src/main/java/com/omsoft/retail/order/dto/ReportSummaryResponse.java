package com.omsoft.retail.order.dto;

import com.omsoft.retail.order.type.OrderStatus;

import java.math.BigDecimal;
import java.util.Map;

public record ReportSummaryResponse(
        long totalOrders,
        BigDecimal totalRevenue,
        Map<OrderStatus, Long> ordersByStatus
) {}
