package com.ordering.restaurant.service;

import com.ordering.restaurant.dto.OrderDTO;
import com.ordering.restaurant.dto.OrderItemDTO;
import com.ordering.restaurant.dto.MenuItemDTO;
import com.ordering.restaurant.model.*;
import com.ordering.restaurant.repository.MenuItemRepository;
import com.ordering.restaurant.repository.OrderItemRepository;
import com.ordering.restaurant.repository.OrderRepository;
import com.ordering.restaurant.repository.TableRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final MenuItemRepository menuItemRepository;
    private final TableRepository tableRepository;

    public OrderService(OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            MenuItemRepository menuItemRepository,
            TableRepository tableRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.menuItemRepository = menuItemRepository;
        this.tableRepository = tableRepository;
    }

    @Transactional
    public OrderDTO createOrder(OrderDTO orderDTO) {
        Order order = new Order();

        RestaurantTable table = tableRepository.findById(orderDTO.getRestaurantTableId())
                .orElseThrow(() -> new EntityNotFoundException("Restaurant table not found"));
        order.setTable(table);
        
        if (orderDTO.getCustomerName() != null) {
            order.setCustomerName(orderDTO.getCustomerName());
        }

        order.setStatus(Order.OrderStatus.PENDING);

        if (orderDTO.getSpecialInstructions() != null) {
            order.setSpecialInstructions(orderDTO.getSpecialInstructions());
        }

        order = orderRepository.save(order);

        List<OrderItem> orderItems = new ArrayList<>();
        for (OrderItemDTO itemDTO : orderDTO.getItems()) {
            OrderItem orderItem = new OrderItem();
            MenuItem menuItem = menuItemRepository.findById(itemDTO.getMenuItemId())
                    .orElseThrow(() -> new EntityNotFoundException("Menu item not found"));

            orderItem.setOrder(order);
            orderItem.setMenuItem(menuItem);
            orderItem.setQuantity(itemDTO.getQuantity());
            if (itemDTO.getSpecialInstructions() != null) {
                orderItem.setSpecialInstructions(itemDTO.getSpecialInstructions());
            }
            orderItem.setPriceAtTimeOfOrder(menuItem.getPrice());
            orderItem.setStatus(OrderItem.OrderItemStatus.PENDING);

            orderItems.add(orderItem);
        }

        orderItemRepository.saveAll(orderItems);
        order.setItems(orderItems);

        return convertToDTO(order);
    }

    @Transactional
    public OrderDTO updateOrderStatus(Long orderId, Order.OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        order.setStatus(newStatus);
        order.setUpdatedAt(LocalDateTime.now());

        return convertToDTO(orderRepository.save(order));
    }

    public OrderDTO getOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));
        return convertToDTO(order);
    }

    @Transactional(readOnly = true)
    public List<OrderDTO> getOrdersByTable(Long tableId) {
        return orderRepository.findByTableId(tableId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrderDTO> getOrdersByStatus(Order.OrderStatus status) {
        return orderRepository.findByStatus(status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private OrderDTO convertToDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setRestaurantTableId(order.getTable().getId());
        dto.setCustomerName(order.getCustomerName());
        dto.setStatus(order.getStatus());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());
        dto.setSpecialInstructions(order.getSpecialInstructions());

        List<OrderItemDTO> itemDTOs = order.getItems().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        dto.setItems(itemDTOs);

        return dto;
    }

    private OrderItemDTO convertToDTO(OrderItem orderItem) {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setId(orderItem.getId());
        dto.setMenuItemId(orderItem.getMenuItem().getId());
        dto.setQuantity(orderItem.getQuantity());
        dto.setSpecialInstructions(orderItem.getSpecialInstructions());
        dto.setPriceAtTimeOfOrder(orderItem.getPriceAtTimeOfOrder());
        dto.setStatus(orderItem.getStatus());
        
        MenuItemDTO menuItemDTO = new MenuItemDTO();
        MenuItem menuItem = orderItem.getMenuItem();
        menuItemDTO.setId(menuItem.getId());
        menuItemDTO.setName(menuItem.getName());
        menuItemDTO.setDescription(menuItem.getDescription());
        menuItemDTO.setPrice(menuItem.getPrice());
        menuItemDTO.setCategoryId(menuItem.getCategory().getId());
        menuItemDTO.setCategoryName(menuItem.getCategoryName());
        menuItemDTO.setAvailable(menuItem.isAvailable());
        menuItemDTO.setImageUrl(menuItem.getImageUrl());
        menuItemDTO.setImageName(menuItem.getImageName());
        menuItemDTO.setImageContentType(menuItem.getImageContentType());
        menuItemDTO.setImageSize(menuItem.getImageSize());
        if (menuItem.getMenu() != null) {
            menuItemDTO.setMenuId(menuItem.getMenu().getId());
        }
        
        dto.setMenuItem(menuItemDTO);
        
        return dto;
    }
}