package com.ordering.restaurant.controller;

import com.ordering.restaurant.dto.QRCodeResponse;
import com.ordering.restaurant.dto.TableDTO;
import com.ordering.restaurant.service.TableService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tables")
@Tag(name = "Tables", description = "Table management endpoints")
public class TableController {

    @Autowired
    private TableService tableService;

    @GetMapping
    @Operation(summary = "Get all tables")
    @PreAuthorize("hasAnyRole('WAITER', 'MANAGER')")
    public ResponseEntity<List<TableDTO>> getAllTables() {
        return ResponseEntity.ok(tableService.getAllTables());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get specific table by ID")
    @PreAuthorize("hasAnyRole('WAITER', 'MANAGER')")
    public ResponseEntity<TableDTO> getTableById(@PathVariable Long id) {
        return ResponseEntity.ok(tableService.getTableById(id));
    }

    @PostMapping
    @Operation(summary = "Add new table")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<TableDTO> createTable(@Valid @RequestBody TableDTO tableDTO) {
        return ResponseEntity.ok(tableService.createTable(tableDTO));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update table")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<TableDTO> updateTable(
            @PathVariable Long id,
            @Valid @RequestBody TableDTO tableDTO) {
        return ResponseEntity.ok(tableService.updateTable(id, tableDTO));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete table")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Void> deleteTable(@PathVariable Long id) {
        tableService.deleteTable(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/status")
    @Operation(summary = "Check if table has active orders")
    @PreAuthorize("hasAnyRole('WAITER', 'MANAGER')")
    public ResponseEntity<Boolean> checkTableStatus(@PathVariable Long id) {
        return ResponseEntity.ok(tableService.hasActiveOrders(id));
    }

    @PostMapping("/qrcode/{id}")
    @Operation(summary = "Generate QR code for table")
    @PreAuthorize("hasAnyRole('WAITER', 'MANAGER')")
    public ResponseEntity<QRCodeResponse> generateQRCode(@PathVariable Long id) {
        return ResponseEntity.ok(tableService.generateQRCode(id));
    }
} 