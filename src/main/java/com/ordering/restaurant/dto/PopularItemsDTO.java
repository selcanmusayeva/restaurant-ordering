package com.ordering.restaurant.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PopularItemsDTO {
    private LocalDate startDate;
    private LocalDate endDate;
    private int totalOrdersAnalyzed;
    private List<PopularItemEntryDTO> items;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PopularItemEntryDTO {
        private Long menuItemId;
        private String menuItemName;
        private String category;
        private int quantitySold;
        private double percentageOfOrders;
    }
} 