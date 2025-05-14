package com.ordering.restaurant.util;

import com.ordering.restaurant.dto.*;
import com.ordering.restaurant.model.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class TestDataBuilder {

    public static User createTestUser() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password123");
        user.setRole(UserRole.WAITER);
        return user;
    }

    public static RestaurantTable createTestTable() {
        RestaurantTable table = new RestaurantTable();
        table.setTableNumber("1");
        table.setCapacity(4);
        table.setStatus(RestaurantTable.TableStatus.AVAILABLE);
        return table;
    }

    public static Category createTestCategory() {
        Category category = new Category();
        category.setName("Test Category");
        category.setDescription("Test Category Description");
        return category;
    }

    public static MenuItem createTestMenuItem(Category category) {
        MenuItem menuItem = new MenuItem();
        menuItem.setName("Test Item");
        menuItem.setDescription("Test Item Description");
        menuItem.setPrice(new BigDecimal("9.99"));
        menuItem.setCategory(category);
        menuItem.setAvailable(true);
        return menuItem;
    }

    public static Order createTestOrder(RestaurantTable table) {
        Order order = new Order();
        order.setTable(table);
        order.setCustomerName("Test Customer");
        order.setStatus(Order.OrderStatus.PENDING);
        order.setCreatedAt(LocalDateTime.now());
        return order;
    }

    public static OrderItem createTestOrderItem(Order order, MenuItem menuItem) {
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setMenuItem(menuItem);
        orderItem.setQuantity(2);
        orderItem.setPriceAtTimeOfOrder(menuItem.getPrice());
        orderItem.setStatus(OrderItem.OrderItemStatus.PENDING);
        return orderItem;
    }

    public static OrderDTO createTestOrderDTO() {
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setRestaurantTableId(1L);
        orderDTO.setCustomerName("Test Customer");
        orderDTO.setSpecialInstructions("Test Instructions");

        OrderItemDTO itemDTO = new OrderItemDTO();
        itemDTO.setMenuItemId(1L);
        itemDTO.setQuantity(2);
        itemDTO.setSpecialInstructions("Test Item Instructions");

        orderDTO.setItems(Arrays.asList(itemDTO));
        return orderDTO;
    }

    public static AuthRequest createTestAuthRequest() {
        return new AuthRequest("testuser", "password123");
    }

    public static RegisterRequest createTestRegisterRequest() {
        RegisterRequest request = new RegisterRequest();
        request.setFullName("Test User");
        request.setUsername("testuser");
        request.setEmail("test@example.com");
        request.setPassword("password123");
        return request;
    }
} 