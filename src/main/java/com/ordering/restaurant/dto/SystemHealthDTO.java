package com.ordering.restaurant.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemHealthDTO {
    private String status;
    private LocalDateTime timestamp;
    private Map<String, String> components;
    private String version;
} 