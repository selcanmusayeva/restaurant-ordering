package com.ordering.restaurant.dto;

import com.ordering.restaurant.model.RestaurantTable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TableDTO {
    private Long id;
    
    @NotBlank(message = "Table number is required")
    private String tableNumber;
    
    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;
    
    private String qrCode;
    
    private RestaurantTable.TableStatus status;
    
    public static TableDTO fromEntity(RestaurantTable table) {
        return TableDTO.builder()
                .id(table.getId())
                .tableNumber(table.getTableNumber())
                .capacity(table.getCapacity())
                .qrCode(table.getQrCode())
                .status(table.getStatus())
                .build();
    }
    
    public RestaurantTable toEntity() {
        RestaurantTable table = new RestaurantTable();
        table.setTableNumber(this.tableNumber);
        table.setCapacity(this.capacity);
        if (this.qrCode != null) {
            table.setQrCode(this.qrCode);
        }
        if (this.status != null) {
            table.setStatus(this.status);
        }
        return table;
    }
} 