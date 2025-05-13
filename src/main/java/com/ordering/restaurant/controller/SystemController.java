package com.ordering.restaurant.controller;

import com.ordering.restaurant.dto.SystemHealthDTO;
import com.ordering.restaurant.dto.SystemStatisticsDTO;
import com.ordering.restaurant.service.SystemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/system")
@Tag(name = "System", description = "System management endpoints")
public class SystemController {

    @Autowired
    private SystemService systemService;

    @GetMapping("/stats")
    @Operation(summary = "Get system statistics")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<SystemStatisticsDTO> getSystemStatistics() {
        return ResponseEntity.ok(systemService.getSystemStatistics());
    }

    @GetMapping("/status")
    @Operation(summary = "Check system health")
    public ResponseEntity<SystemHealthDTO> getSystemHealth() {
        return ResponseEntity.ok(systemService.getSystemHealth());
    }
} 