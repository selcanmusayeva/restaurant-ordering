package com.ordering.restaurant.service;

import com.ordering.restaurant.dto.SystemHealthDTO;
import com.ordering.restaurant.dto.SystemStatisticsDTO;
import com.ordering.restaurant.model.Order;
import com.ordering.restaurant.model.RestaurantTable;
import com.ordering.restaurant.model.User;
import com.ordering.restaurant.repository.MenuItemRepository;
import com.ordering.restaurant.repository.OrderRepository;
import com.ordering.restaurant.repository.TableRepository;
import com.ordering.restaurant.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SystemService {

        @Autowired
        private OrderRepository orderRepository;

        @Autowired
        private UserRepository userRepository;

        @Autowired
        private TableRepository tableRepository;

        @Autowired
        private MenuItemRepository menuItemRepository;

        @Value("${app.version:1.0.0}")
        private String appVersion;

        public SystemStatisticsDTO getSystemStatistics() {
                LocalDateTime startOfDay = LocalDateTime.now().toLocalDate().atStartOfDay();
                LocalDateTime startOfWeek = LocalDateTime.now().minusWeeks(1);
                LocalDateTime startOfMonth = LocalDateTime.now().minusMonths(1);

                List<Order> ordersToday = orderRepository.findByCreatedAtAfter(startOfDay);

                List<RestaurantTable> occupiedTables = tableRepository.findAll().stream()
                                .filter(t -> t.getStatus() == RestaurantTable.TableStatus.OCCUPIED)
                                .collect(Collectors.toList());

                int activeUsers = (int) userRepository.findAll().stream().filter(User::isEnabled).count();

                int ordersInProgress = (int) ordersToday.stream()
                                .filter(o -> o.getStatus() == Order.OrderStatus.IN_PROGRESS ||
                                                o.getStatus() == Order.OrderStatus.PENDING ||
                                                o.getStatus() == Order.OrderStatus.CONFIRMED)
                                .count();

                Map<String, Integer> popularItems = new HashMap<>();

                BigDecimal totalSalesToday = calculateTotalSales(ordersToday);

                return SystemStatisticsDTO.builder()
                                .totalActiveUsers(activeUsers)
                                .totalTablesOccupied(occupiedTables.size())
                                .totalOrdersToday(ordersToday.size())
                                .totalOrdersInProgress(ordersInProgress)
                                .totalMenuItems(menuItemRepository.findAll().size())
                                .totalSalesToday(totalSalesToday)
                                .totalSalesWeek(BigDecimal.ZERO)
                                .totalSalesMonth(BigDecimal.ZERO)
                                .lastOrderTime(ordersToday.isEmpty() ? null
                                                : ordersToday.get(ordersToday.size() - 1).getCreatedAt())
                                .popularItems(popularItems)
                                .salesByCategory(new HashMap<>())
                                .salesByHour(new HashMap<>())
                                .build();
        }

        public SystemHealthDTO getSystemHealth() {
                Map<String, String> components = new HashMap<>();
                components.put("database", "UP");
                components.put("api", "UP");

                return SystemHealthDTO.builder()
                                .status("UP")
                                .timestamp(LocalDateTime.now())
                                .components(components)
                                .version(appVersion)
                                .build();
        }

        private BigDecimal calculateTotalSales(List<Order> orders) {
                return orders.stream()
                                .flatMap(order -> order.getItems().stream())
                                .map(item -> item.getPriceAtTimeOfOrder().multiply(new BigDecimal(item.getQuantity())))
                                .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
}