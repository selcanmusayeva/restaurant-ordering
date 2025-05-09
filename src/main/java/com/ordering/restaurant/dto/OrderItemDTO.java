package com.ordering.restaurant.dto;

import com.ordering.restaurant.model.OrderItem;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderItemDTO {
    private Long id;

    @NotNull(message = "Menu item ID is required")
    private Long menuItemId;

    // Full menu item details
    private MenuItemDTO menuItem;

    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    private String specialInstructions;
    private BigDecimal priceAtTimeOfOrder;
    private OrderItem.OrderItemStatus status;
} 