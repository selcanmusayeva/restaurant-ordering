package com.ordering.restaurant.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemStatisticsDTO {
    private int totalActiveUsers;
    private int totalTablesOccupied;
    private int totalOrdersToday;
    private int totalOrdersInProgress;
    private int totalMenuItems;
    private BigDecimal totalSalesToday;
    private BigDecimal totalSalesWeek;
    private BigDecimal totalSalesMonth;
    private LocalDateTime lastOrderTime;
    private Map<String, Integer> popularItems;
    private Map<String, BigDecimal> salesByCategory;
    private Map<String, BigDecimal> salesByHour;
} 