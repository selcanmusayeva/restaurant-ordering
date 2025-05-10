package com.ordering.restaurant.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SalesReportDTO {
    private LocalDate startDate;
    private LocalDate endDate;
    private int totalOrders;
    private BigDecimal totalRevenue;
    private BigDecimal averageOrderValue;
    private int totalItemsSold;
    private Map<String, BigDecimal> salesByCategory;
    private Map<String, Integer> salesByMenuItem;
    private List<SalesByHourDTO> salesByHour;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SalesByHourDTO {
        private int hour;
        private int orderCount;
        private BigDecimal revenue;
    }
} 