package com.ordering.restaurant.controller;

import com.ordering.restaurant.dto.AveragePreparationTimeDTO;
import com.ordering.restaurant.dto.PopularItemsDTO;
import com.ordering.restaurant.dto.SalesReportDTO;
import com.ordering.restaurant.service.ReportsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/reports")
@Tag(name = "Reports", description = "Reporting endpoints")
@PreAuthorize("hasRole('MANAGER')")
public class ReportsController {

    @Autowired
    private ReportsService reportsService;

    @GetMapping("/sales/daily")
    @Operation(summary = "Get daily sales report")
    public ResponseEntity<SalesReportDTO> getDailySalesReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(reportsService.getDailySalesReport(date == null ? LocalDate.now() : date));
    }

    @GetMapping("/sales/weekly")
    @Operation(summary = "Get weekly sales report")
    public ResponseEntity<SalesReportDTO> getWeeklySalesReport(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {
        return ResponseEntity.ok(reportsService.getWeeklySalesReport(startDate == null ? LocalDate.now() : startDate));
    }

    @GetMapping("/popular-items")
    @Operation(summary = "Get popular items report")
    public ResponseEntity<PopularItemsDTO> getPopularItems(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "10") int limit) {
        
        if (startDate == null) {
            startDate = LocalDate.now().minusMonths(1);
        }
        
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        
        return ResponseEntity.ok(reportsService.getPopularItems(startDate, endDate, limit));
    }

    @GetMapping("/average-preparation-time")
    @Operation(summary = "Get average preparation time report")
    public ResponseEntity<AveragePreparationTimeDTO> getAveragePreparationTime(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        
        if (startDate == null) {
            startDate = LocalDate.now().minusMonths(1);
        }
        
        if (endDate == null) {
            endDate = LocalDate.now();
        }
        
        return ResponseEntity.ok(reportsService.getAveragePreparationTime(startDate, endDate));
    }
} 