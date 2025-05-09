package com.ordering.restaurant.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class MenuDTO {
    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    private String description;
    private boolean active = true;
    private List<MenuItemDTO> items;
} 