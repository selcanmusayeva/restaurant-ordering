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
public class NotificationDTO {
    private Long id;
    private String message;
    private String type;
    private Long referenceId;
    private String referenceType;
    private boolean read;
    private LocalDateTime createdAt;
    private String targetUsername;
} 