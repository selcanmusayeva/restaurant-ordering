package com.ordering.restaurant.dto;

import com.ordering.restaurant.model.TableAssignment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TableAssignmentDTO {
    private Long id;
    
    @NotNull(message = "Table ID is required")
    private Long tableId;
    
    private String tableNumber;
    
    @NotNull(message = "Waiter username is required")
    private String waiterUsername;
    
    private String waiterFullName;
    
    private LocalDateTime assignedAt;
    
    @NotNull(message = "Shift date is required")
    private LocalDateTime shiftDate;
    
    private boolean active;
    
    public static TableAssignmentDTO fromEntity(TableAssignment assignment) {
        return TableAssignmentDTO.builder()
                .id(assignment.getId())
                .tableId(assignment.getTable().getId())
                .tableNumber(assignment.getTable().getTableNumber())
                .waiterUsername(assignment.getWaiter().getUsername())
                .waiterFullName(assignment.getWaiter().getFullName())
                .assignedAt(assignment.getAssignedAt())
                .shiftDate(assignment.getShiftDate())
                .active(assignment.isActive())
                .build();
    }
} 