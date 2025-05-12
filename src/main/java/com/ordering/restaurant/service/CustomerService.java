package com.ordering.restaurant.service;

import com.ordering.restaurant.dto.CustomerSessionDTO;
import com.ordering.restaurant.dto.OrderStatusDTO;
import com.ordering.restaurant.exception.ResourceNotFoundException;
import com.ordering.restaurant.model.Order;
import com.ordering.restaurant.model.RestaurantTable;
import com.ordering.restaurant.repository.OrderRepository;
import com.ordering.restaurant.repository.TableRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class CustomerService {

    private final TableRepository tableRepository;
    private final OrderRepository orderRepository;

    private final Map<String, CustomerSessionDTO> activeSessions = new HashMap<>();

    public CustomerService(TableRepository tableRepository, OrderRepository orderRepository) {
        this.tableRepository = tableRepository;
        this.orderRepository = orderRepository;
    }

    public CustomerSessionDTO startCustomerSession(Long tableId, String qrCode) {
        RestaurantTable table = tableRepository.findById(tableId)
                .orElseThrow(() -> new ResourceNotFoundException("Table not found with id: " + tableId));

        if (!table.getQrCode().equals(qrCode)) {
            throw new IllegalArgumentException("Invalid QR code for table");
        }

        String sessionId = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusHours(4);

        CustomerSessionDTO session = CustomerSessionDTO.builder()
                .sessionId(sessionId)
                .tableId(tableId)
                .tableNumber(table.getTableNumber())
                .createdAt(now)
                .expiresAt(expiresAt)
                .build();

        activeSessions.put(sessionId, session);
        return session;
    }

    public void validateSession(Long tableId, String sessionId) {
        CustomerSessionDTO session = activeSessions.get(sessionId);
        if (session == null || session.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Invalid or expired session");
        }

        if (!session.getTableId().equals(tableId)) {
            throw new IllegalArgumentException("Session does not match table");
        }
    }

    public OrderStatusDTO getOrderStatus(Long orderId, String sessionId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));

        CustomerSessionDTO session = activeSessions.get(sessionId);
        if (session == null || session.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Invalid or expired session");
        }

        if (!session.getTableId().equals(order.getTable().getId())) {
            throw new IllegalArgumentException("Session does not match order's table");
        }

        String estimatedTime = getEstimatedPreparationTime(order.getStatus());

        return OrderStatusDTO.builder()
                .orderId(order.getId())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .tableId(order.getTable().getId())
                .estimatedPreparationTime(estimatedTime)
                .build();
    }

    private String getEstimatedPreparationTime(Order.OrderStatus status) {
        switch (status) {
            case PENDING:
                return "15-20 minutes";
            case CONFIRMED:
                return "10-15 minutes";
            case IN_PROGRESS:
                return "5-10 minutes";
            case READY:
                return "Ready for pickup";
            case DELIVERED:
                return "Already delivered";
            case CANCELLED:
                return "Order cancelled";
            default:
                return "Unknown";
        }
    }
}