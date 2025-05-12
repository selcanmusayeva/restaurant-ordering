package com.ordering.restaurant.service;

import com.ordering.restaurant.dto.KitchenStatisticsDTO;
import com.ordering.restaurant.dto.OrderDTO;
import com.ordering.restaurant.model.Order;
import com.ordering.restaurant.model.OrderItem;
import com.ordering.restaurant.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class KitchenService {

    private final OrderRepository orderRepository;
    private final OrderService orderService;

    public KitchenService(OrderRepository orderRepository,
            OrderService orderService) {
        this.orderRepository = orderRepository;
        this.orderService = orderService;
    }

    public List<OrderDTO> getIncomingOrders() {
        List<Order.OrderStatus> statuses = List.of(
                Order.OrderStatus.PENDING,
                Order.OrderStatus.CONFIRMED,
                Order.OrderStatus.IN_PROGRESS,
                Order.OrderStatus.DELIVERED,
                Order.OrderStatus.READY,
                Order.OrderStatus.CANCELLED);

        return orderRepository.findByStatusIn(statuses).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<OrderDTO> getOrdersByStatus(Order.OrderStatus status) {
        return orderService.getOrdersByStatus(status);
    }

    @Transactional
    public OrderDTO updateOrderStatus(Long orderId, Order.OrderStatus status) {
        return orderService.updateOrderStatus(orderId, status);
    }

    public KitchenStatisticsDTO getKitchenStatistics() {
        LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
        List<Order> ordersToday = orderRepository.findByCreatedAtAfter(startOfDay);

        int totalOrdersToday = ordersToday.size();
        int pendingOrders = (int) ordersToday.stream()
                .filter(o -> o.getStatus() == Order.OrderStatus.PENDING ||
                        o.getStatus() == Order.OrderStatus.CONFIRMED)
                .count();

        int inProgressOrders = (int) ordersToday.stream()
                .filter(o -> o.getStatus() == Order.OrderStatus.IN_PROGRESS)
                .count();

        int completedOrders = (int) ordersToday.stream()
                .filter(o -> o.getStatus() == Order.OrderStatus.DELIVERED ||
                        o.getStatus() == Order.OrderStatus.READY)
                .count();

        Duration averagePreparationTime = calculateAveragePreparationTime(ordersToday);

        Map<String, Integer> ordersByMenuItem = calculateOrdersByMenuItem(ordersToday);

        return KitchenStatisticsDTO.builder()
                .totalOrdersToday(totalOrdersToday)
                .pendingOrders(pendingOrders)
                .inProgressOrders(inProgressOrders)
                .completedOrders(completedOrders)
                .averagePreparationTime(averagePreparationTime)
                .ordersByMenuItem(ordersByMenuItem)
                .build();
    }

    private OrderDTO mapToDTO(Order order) {
        return orderService.getOrder(order.getId());
    }

    private Duration calculateAveragePreparationTime(List<Order> orders) {
        List<Order> completedOrders = orders.stream()
                .filter(o -> o.getStatus() == Order.OrderStatus.DELIVERED ||
                        o.getStatus() == Order.OrderStatus.READY)
                .filter(o -> o.getUpdatedAt() != null)
                .collect(Collectors.toList());

        if (completedOrders.isEmpty()) {
            return Duration.ZERO;
        }

        long totalSeconds = completedOrders.stream()
                .mapToLong(o -> Duration.between(o.getCreatedAt(), o.getUpdatedAt()).getSeconds())
                .sum();

        return Duration.ofSeconds(totalSeconds / completedOrders.size());
    }

    private Map<String, Integer> calculateOrdersByMenuItem(List<Order> orders) {
        Map<String, Integer> result = new HashMap<>();

        for (Order order : orders) {
            for (OrderItem item : order.getItems()) {
                String menuItemName = item.getMenuItem().getName();
                result.put(menuItemName, result.getOrDefault(menuItemName, 0) + item.getQuantity());
            }
        }

        return result;
    }
}