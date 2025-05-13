package com.ordering.restaurant.controller;

import com.ordering.restaurant.dto.CustomerSessionDTO;
import com.ordering.restaurant.dto.MenuItemDTO;
import com.ordering.restaurant.dto.OrderDTO;
import com.ordering.restaurant.dto.OrderStatusDTO;
import com.ordering.restaurant.service.CustomerService;
import com.ordering.restaurant.service.MenuService;
import com.ordering.restaurant.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customer")
@Tag(name = "Customer", description = "Customer-specific endpoints")
public class CustomerController {

    @Autowired
    private MenuService menuService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private CustomerService customerService;

    @GetMapping("/menu")
    @Operation(summary = "Get optimized menu view for customers")
    public ResponseEntity<List<MenuItemDTO>> getCustomerMenu() {
        return ResponseEntity.ok(menuService.getAvailableItems());
    }

    @PostMapping("/table/{tableId}/session")
    @Operation(summary = "Start customer session")
    public ResponseEntity<CustomerSessionDTO> startCustomerSession(
            @PathVariable Long tableId,
            @RequestParam String qrCode) {
        return ResponseEntity.ok(customerService.startCustomerSession(tableId, qrCode));
    }

    @GetMapping("/table/{tableId}/orders")
    @Operation(summary = "View orders for this table")
    public ResponseEntity<List<OrderDTO>> getOrdersForTable(
            @PathVariable Long tableId,
            @RequestParam String sessionId) {
        customerService.validateSession(tableId, sessionId);
        return ResponseEntity.ok(orderService.getOrdersByTable(tableId));
    }

    @GetMapping("/order/{orderId}/status")
    @Operation(summary = "Check specific order status")
    public ResponseEntity<OrderStatusDTO> getOrderStatus(
            @PathVariable Long orderId,
            @RequestParam String sessionId) {
        return ResponseEntity.ok(customerService.getOrderStatus(orderId, sessionId));
    }

    @PostMapping("/table/{tableId}/order")
    @Operation(summary = "Create a new order as customer")
    public ResponseEntity<OrderDTO> createOrder(
            @PathVariable Long tableId,
            @RequestParam String sessionId,
            @Valid @RequestBody OrderDTO orderDTO) {
        customerService.validateSession(tableId, sessionId);
        orderDTO.setRestaurantTableId(tableId);
        return ResponseEntity.ok(orderService.createOrder(orderDTO));
    }
}
