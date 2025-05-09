package com.ordering.restaurant.dto;

import com.ordering.restaurant.model.Order;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDTO {
    private Long id;

    @NotNull(message = "Restaurant table ID is required")
    private Long restaurantTableId;

    private String customerName;

    private Order.OrderStatus status;
    private String specialInstructions;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<OrderItemDTO> items;
} 