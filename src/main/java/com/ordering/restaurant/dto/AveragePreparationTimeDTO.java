package com.ordering.restaurant.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AveragePreparationTimeDTO {
    private LocalDate startDate;
    private LocalDate endDate;
    private int totalOrdersAnalyzed;
    private Duration overallAverageTime;
    private Map<String, Duration> averageTimeByCategory;
    private Map<String, Duration> averageTimeByMenuItem;
    private List<PreparationTimeByHourDTO> averageTimeByHour;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PreparationTimeByHourDTO {
        private int hour;
        private int orderCount;
        private Duration averageTime;
    }
}