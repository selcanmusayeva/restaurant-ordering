package com.ordering.restaurant.controller;

import com.ordering.restaurant.dto.KitchenStatisticsDTO;
import com.ordering.restaurant.dto.OrderDTO;
import com.ordering.restaurant.model.Order;
import com.ordering.restaurant.service.KitchenService;
import com.ordering.restaurant.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/kitchen")
@Tag(name = "Kitchen", description = "Kitchen display system endpoints")
public class KitchenController {

    @Autowired
    private KitchenService kitchenService;
    
    @Autowired
    private OrderService orderService;

    @GetMapping("/orders")
    @Operation(summary = "Get all incoming orders")
    public ResponseEntity<List<OrderDTO>> getIncomingOrders() {
        return ResponseEntity.ok(kitchenService.getIncomingOrders());
    }
    
    @GetMapping("/orders/{id}")
    @Operation(summary = "Get order by ID")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrder(id));
    }

    @GetMapping("/orders/pending")
    @Operation(summary = "Get orders waiting to be prepared")
    public ResponseEntity<List<OrderDTO>> getPendingOrders() {
        return ResponseEntity.ok(kitchenService.getOrdersByStatus(Order.OrderStatus.PENDING));
    }

    @GetMapping("/orders/in-progress")
    @Operation(summary = "Get orders being prepared")
    public ResponseEntity<List<OrderDTO>> getInProgressOrders() {
        return ResponseEntity.ok(kitchenService.getOrdersByStatus(Order.OrderStatus.IN_PROGRESS));
    }

    @PutMapping("/orders/{id}/preparation")
    @Operation(summary = "Mark order as in preparation")
    @PreAuthorize("hasRole('CHEF') or hasRole('MANAGER')")
    public ResponseEntity<OrderDTO> markOrderAsInPreparation(@PathVariable Long id) {
        return ResponseEntity.ok(kitchenService.updateOrderStatus(id, Order.OrderStatus.IN_PROGRESS));
    }

    @PutMapping("/orders/{id}/ready")
    @Operation(summary = "Mark order as ready")
    @PreAuthorize("hasRole('CHEF') or hasRole('MANAGER')")
    public ResponseEntity<OrderDTO> markOrderAsReady(@PathVariable Long id) {
        return ResponseEntity.ok(kitchenService.updateOrderStatus(id, Order.OrderStatus.READY));
    }

    @GetMapping("/statistics")
    @Operation(summary = "Get kitchen performance statistics")
    @PreAuthorize("hasRole('CHEF') or hasRole('MANAGER')")
    public ResponseEntity<KitchenStatisticsDTO> getKitchenStatistics() {
        return ResponseEntity.ok(kitchenService.getKitchenStatistics());
    }
} 