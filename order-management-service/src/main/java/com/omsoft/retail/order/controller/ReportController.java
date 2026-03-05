package com.omsoft.retail.order.controller;

import com.omsoft.retail.order.dto.ReportSummaryResponse;
import com.omsoft.retail.order.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Reports", description = "Sales and order reports")
@RestController
@RequestMapping("/api/order/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @Operation(summary = "Get order summary report")
    @GetMapping("/summary")
    public ReportSummaryResponse getSummary() {
        return reportService.getSummary();
    }
}
