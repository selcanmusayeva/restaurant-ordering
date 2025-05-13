package com.ordering.restaurant.controller;

import com.ordering.restaurant.service.TableService;
import com.ordering.restaurant.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/manager")
@Tag(name = "Manager", description = "Manager-specific operations")
@PreAuthorize("hasRole('MANAGER')")
public class ManagerController {

    private final TableService tableService;
    private final UserService userService;

    public ManagerController(TableService tableService, UserService userService) {
        this.tableService = tableService;
        this.userService = userService;
    }

    @PostMapping("/tables/{tableId}/assign/{username}")
    @Operation(summary = "Assign a table to a specific waiter")
    public ResponseEntity<Void> assignTableToWaiter(
            @PathVariable Long tableId,
            @PathVariable String username,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime shiftDate) {
        
        tableService.assignTableToWaiter(tableId, username, shiftDate);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/tables/{tableId}/assign")
    @Operation(summary = "Unassign a table from any waiter")
    public ResponseEntity<Void> unassignTable(@PathVariable Long tableId) {
        tableService.unassignTableFromWaiter(tableId);
        return ResponseEntity.ok().build();
    }
} 