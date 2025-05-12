package com.ordering.restaurant.service;

import com.ordering.restaurant.dto.AveragePreparationTimeDTO;
import com.ordering.restaurant.dto.PopularItemsDTO;
import com.ordering.restaurant.dto.SalesReportDTO;
import com.ordering.restaurant.model.MenuItem;
import com.ordering.restaurant.model.Order;
import com.ordering.restaurant.model.OrderItem;
import com.ordering.restaurant.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportsService {

    private final OrderRepository orderRepository;

    public ReportsService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public SalesReportDTO getDailySalesReport(LocalDate date) {
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        return generateSalesReport(startOfDay, endOfDay, date, date);
    }

    public SalesReportDTO getWeeklySalesReport(LocalDate startDate) {
        LocalDate endDate = startDate.plusDays(6);
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        return generateSalesReport(startDateTime, endDateTime, startDate, endDate);
    }

    private SalesReportDTO generateSalesReport(LocalDateTime startDateTime, LocalDateTime endDateTime,
            LocalDate startDate, LocalDate endDate) {
        List<Order> orders = orderRepository.findByCreatedAtBetween(startDateTime, endDateTime).stream()
                .filter(o -> o.getStatus() != Order.OrderStatus.CANCELLED)
                .collect(Collectors.toList());

        if (orders.isEmpty()) {
            return SalesReportDTO.builder()
                    .startDate(startDate)
                    .endDate(endDate)
                    .totalOrders(0)
                    .totalRevenue(BigDecimal.ZERO)
                    .averageOrderValue(BigDecimal.ZERO)
                    .totalItemsSold(0)
                    .salesByCategory(new HashMap<>())
                    .salesByMenuItem(new HashMap<>())
                    .salesByHour(new ArrayList<>())
                    .build();
        }

        BigDecimal totalRevenue = BigDecimal.ZERO;
        int totalItemsSold = 0;
        Map<String, BigDecimal> salesByCategory = new HashMap<>();
        Map<String, Integer> salesByMenuItem = new HashMap<>();
        Map<Integer, Integer> orderCountByHour = new HashMap<>();
        Map<Integer, BigDecimal> revenueByHour = new HashMap<>();

        for (Order order : orders) {
            int orderHour = order.getCreatedAt().getHour();
            orderCountByHour.put(orderHour, orderCountByHour.getOrDefault(orderHour, 0) + 1);

            for (OrderItem item : order.getItems()) {
                BigDecimal itemTotal = item.getPriceAtTimeOfOrder().multiply(BigDecimal.valueOf(item.getQuantity()));
                totalRevenue = totalRevenue.add(itemTotal);
                totalItemsSold += item.getQuantity();

                MenuItem menuItem = item.getMenuItem();
                String category = menuItem.getCategory().getName();
                String menuItemName = menuItem.getName();

                salesByCategory.put(category,
                        salesByCategory.getOrDefault(category, BigDecimal.ZERO).add(itemTotal));

                salesByMenuItem.put(menuItemName,
                        salesByMenuItem.getOrDefault(menuItemName, 0) + item.getQuantity());

                revenueByHour.put(orderHour,
                        revenueByHour.getOrDefault(orderHour, BigDecimal.ZERO).add(itemTotal));
            }
        }

        BigDecimal averageOrderValue = totalRevenue.divide(BigDecimal.valueOf(orders.size()), 2, RoundingMode.HALF_UP);

        List<SalesReportDTO.SalesByHourDTO> salesByHour = new ArrayList<>();
        for (int hour = 0; hour < 24; hour++) {
            if (orderCountByHour.containsKey(hour)) {
                salesByHour.add(SalesReportDTO.SalesByHourDTO.builder()
                        .hour(hour)
                        .orderCount(orderCountByHour.getOrDefault(hour, 0))
                        .revenue(revenueByHour.getOrDefault(hour, BigDecimal.ZERO))
                        .build());
            }
        }

        return SalesReportDTO.builder()
                .startDate(startDate)
                .endDate(endDate)
                .totalOrders(orders.size())
                .totalRevenue(totalRevenue)
                .averageOrderValue(averageOrderValue)
                .totalItemsSold(totalItemsSold)
                .salesByCategory(salesByCategory)
                .salesByMenuItem(salesByMenuItem)
                .salesByHour(salesByHour)
                .build();
    }

    public PopularItemsDTO getPopularItems(LocalDate startDate, LocalDate endDate, int limit) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        List<Order> orders = orderRepository.findByCreatedAtBetween(startDateTime, endDateTime).stream()
                .filter(o -> o.getStatus() != Order.OrderStatus.CANCELLED)
                .collect(Collectors.toList());

        if (orders.isEmpty()) {
            return PopularItemsDTO.builder()
                    .startDate(startDate)
                    .endDate(endDate)
                    .totalOrdersAnalyzed(0)
                    .items(new ArrayList<>())
                    .build();
        }

        Map<Long, Integer> itemQuantities = new HashMap<>();
        Map<Long, String> itemNames = new HashMap<>();
        Map<Long, String> itemCategories = new HashMap<>();

        for (Order order : orders) {
            for (OrderItem item : order.getItems()) {
                Long menuItemId = item.getMenuItem().getId();
                itemQuantities.put(menuItemId, itemQuantities.getOrDefault(menuItemId, 0) + item.getQuantity());
                itemNames.put(menuItemId, item.getMenuItem().getName());
                itemCategories.put(menuItemId, item.getMenuItem().getCategory().getName());
            }
        }

        List<Map.Entry<Long, Integer>> sortedItems = new ArrayList<>(itemQuantities.entrySet());
        sortedItems.sort(Map.Entry.<Long, Integer>comparingByValue().reversed());
        if (sortedItems.size() > limit) {
            sortedItems = sortedItems.subList(0, limit);
        }

        int totalOrders = orders.size();
        List<PopularItemsDTO.PopularItemEntryDTO> items = new ArrayList<>();

        for (Map.Entry<Long, Integer> entry : sortedItems) {
            Long menuItemId = entry.getKey();
            int quantity = entry.getValue();
            double percentage = (double) quantity / totalOrders * 100.0;

            items.add(PopularItemsDTO.PopularItemEntryDTO.builder()
                    .menuItemId(menuItemId)
                    .menuItemName(itemNames.get(menuItemId))
                    .category(itemCategories.get(menuItemId))
                    .quantitySold(quantity)
                    .percentageOfOrders(Math.round(percentage * 100.0) / 100.0)
                    .build());
        }

        return PopularItemsDTO.builder()
                .startDate(startDate)
                .endDate(endDate)
                .totalOrdersAnalyzed(totalOrders)
                .items(items)
                .build();
    }

    public AveragePreparationTimeDTO getAveragePreparationTime(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        List<Order> orders = orderRepository.findByCreatedAtBetween(startDateTime, endDateTime).stream()
                .filter(o -> o.getStatus() == Order.OrderStatus.DELIVERED || o.getStatus() == Order.OrderStatus.READY)
                .filter(o -> o.getUpdatedAt() != null)
                .collect(Collectors.toList());

        if (orders.isEmpty()) {
            return AveragePreparationTimeDTO.builder()
                    .startDate(startDate)
                    .endDate(endDate)
                    .totalOrdersAnalyzed(0)
                    .overallAverageTime(Duration.ZERO)
                    .averageTimeByCategory(new HashMap<>())
                    .averageTimeByMenuItem(new HashMap<>())
                    .averageTimeByHour(new ArrayList<>())
                    .build();
        }

        long totalSeconds = orders.stream()
                .mapToLong(o -> Duration.between(o.getCreatedAt(), o.getUpdatedAt()).getSeconds())
                .sum();

        Duration overallAverageTime = Duration.ofSeconds(totalSeconds / orders.size());

        Map<String, List<Duration>> durationsByCategory = new HashMap<>();
        Map<String, List<Duration>> durationsByMenuItem = new HashMap<>();
        Map<Integer, List<Duration>> durationsByHour = new HashMap<>();
        Map<Integer, Integer> orderCountByHour = new HashMap<>();

        for (Order order : orders) {
            Duration orderDuration = Duration.between(order.getCreatedAt(), order.getUpdatedAt());
            int orderHour = order.getCreatedAt().getHour();

            orderCountByHour.put(orderHour, orderCountByHour.getOrDefault(orderHour, 0) + 1);

            if (!durationsByHour.containsKey(orderHour)) {
                durationsByHour.put(orderHour, new ArrayList<>());
            }
            durationsByHour.get(orderHour).add(orderDuration);

            for (OrderItem item : order.getItems()) {
                MenuItem menuItem = item.getMenuItem();
                String category = menuItem.getCategory().getName();
                String menuItemName = menuItem.getName();

                if (!durationsByCategory.containsKey(category)) {
                    durationsByCategory.put(category, new ArrayList<>());
                }
                durationsByCategory.get(category).add(orderDuration);

                if (!durationsByMenuItem.containsKey(menuItemName)) {
                    durationsByMenuItem.put(menuItemName, new ArrayList<>());
                }
                durationsByMenuItem.get(menuItemName).add(orderDuration);
            }
        }

        Map<String, Duration> averageTimeByCategory = durationsByCategory.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> calculateAverageDuration(e.getValue())));

        Map<String, Duration> averageTimeByMenuItem = durationsByMenuItem.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> calculateAverageDuration(e.getValue())));

        List<AveragePreparationTimeDTO.PreparationTimeByHourDTO> averageTimeByHour = new ArrayList<>();
        for (int hour = 0; hour < 24; hour++) {
            if (durationsByHour.containsKey(hour)) {
                averageTimeByHour.add(AveragePreparationTimeDTO.PreparationTimeByHourDTO.builder()
                        .hour(hour)
                        .orderCount(orderCountByHour.getOrDefault(hour, 0))
                        .averageTime(calculateAverageDuration(durationsByHour.get(hour)))
                        .build());
            }
        }

        return AveragePreparationTimeDTO.builder()
                .startDate(startDate)
                .endDate(endDate)
                .totalOrdersAnalyzed(orders.size())
                .overallAverageTime(overallAverageTime)
                .averageTimeByCategory(averageTimeByCategory)
                .averageTimeByMenuItem(averageTimeByMenuItem)
                .averageTimeByHour(averageTimeByHour)
                .build();
    }

    private Duration calculateAverageDuration(List<Duration> durations) {
        if (durations.isEmpty()) {
            return Duration.ZERO;
        }

        long totalSeconds = durations.stream()
                .mapToLong(Duration::getSeconds)
                .sum();

        return Duration.ofSeconds(totalSeconds / durations.size());
    }
}