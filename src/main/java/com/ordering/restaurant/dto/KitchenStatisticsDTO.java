package com.ordering.restaurant.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KitchenStatisticsDTO {
    private int totalOrdersToday;
    private int pendingOrders;
    private int inProgressOrders;
    private int completedOrders;
    private Duration averagePreparationTime;
    private Map<String, Integer> ordersByMenuItem;
} 