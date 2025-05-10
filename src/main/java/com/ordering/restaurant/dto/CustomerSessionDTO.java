package com.ordering.restaurant.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerSessionDTO {
    private String sessionId;
    private Long tableId;
    private String tableNumber;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
} 