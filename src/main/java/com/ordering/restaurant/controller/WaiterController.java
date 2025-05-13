package com.ordering.restaurant.controller;

import com.ordering.restaurant.dto.NotificationDTO;
import com.ordering.restaurant.dto.OrderDTO;
import com.ordering.restaurant.dto.TableDTO;
import com.ordering.restaurant.model.Order;
import com.ordering.restaurant.service.NotificationService;
import com.ordering.restaurant.service.OrderService;
import com.ordering.restaurant.service.TableService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/waiter")
@Tag(name = "Waiter", description = "Waiter notification endpoints")
public class WaiterController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private TableService tableService;

    @GetMapping("/notifications")
    @Operation(summary = "Get pending notifications for waiter")
    @PreAuthorize("hasRole('WAITER') or hasRole('MANAGER')")
    public ResponseEntity<List<NotificationDTO>> getPendingNotifications(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(notificationService.getPendingNotificationsForUser(userDetails.getUsername()));
    }

    @GetMapping("/orders/ready")
    @Operation(summary = "Get orders ready for delivery")
    public ResponseEntity<List<OrderDTO>> getOrdersReadyForDelivery() {
        return ResponseEntity.ok(orderService.getOrdersByStatus(Order.OrderStatus.READY));
    }

    @PutMapping("/orders/{id}/delivered")
    @Operation(summary = "Mark order as delivered")
    @PreAuthorize("hasRole('WAITER') or hasRole('MANAGER')")
    public ResponseEntity<OrderDTO> markOrderAsDelivered(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.updateOrderStatus(id, Order.OrderStatus.DELIVERED));
    }

    @GetMapping("/tables")
    @Operation(summary = "Get tables assigned to waiter")
    @PreAuthorize("hasRole('WAITER') or hasRole('MANAGER')")
    public ResponseEntity<List<TableDTO>> getTablesAssignedToWaiter(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(tableService.getTablesAssignedToWaiter(userDetails.getUsername()));
    }

    @PutMapping("/notifications/{id}/read")
    @Operation(summary = "Mark notification as read")
    @PreAuthorize("hasRole('WAITER') or hasRole('MANAGER')")
    public ResponseEntity<NotificationDTO> markNotificationAsRead(@PathVariable Long id) {
        return ResponseEntity.ok(notificationService.markNotificationAsRead(id));
    }

    @PostMapping("/tables/{tableId}/assign")
    @Operation(summary = "Assign table to waiter")
    @PreAuthorize("hasRole('WAITER') or hasRole('MANAGER')")
    public ResponseEntity<Void> assignTable(
            @PathVariable Long tableId,
            @AuthenticationPrincipal UserDetails userDetails) {

        LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        tableService.assignTableToWaiter(tableId, userDetails.getUsername(), today);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/tables/{tableId}/assign")
    @Operation(summary = "Unassign table from waiter")
    @PreAuthorize("hasRole('WAITER') or hasRole('MANAGER')")
    public ResponseEntity<Void> unassignTable(@PathVariable Long tableId) {
        tableService.unassignTableFromWaiter(tableId);
        return ResponseEntity.ok().build();
    }
}