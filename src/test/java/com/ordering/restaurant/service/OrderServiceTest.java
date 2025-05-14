package com.ordering.restaurant.service;

import com.ordering.restaurant.dto.OrderDTO;
import com.ordering.restaurant.dto.OrderItemDTO;
import com.ordering.restaurant.model.*;
import com.ordering.restaurant.repository.*;
import com.ordering.restaurant.util.TestDataBuilder;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private MenuItemRepository menuItemRepository;

    @Mock
    private TableRepository tableRepository;

    @InjectMocks
    private OrderService orderService;

    private RestaurantTable testTable;
    private MenuItem testMenuItem;
    private Order testOrder;
    private OrderDTO testOrderDTO;

    @BeforeEach
    void setUp() {
        testTable = TestDataBuilder.createTestTable();
        testTable.setId(1L);
        
        Category category = TestDataBuilder.createTestCategory();
        category.setId(1L);
        
        testMenuItem = TestDataBuilder.createTestMenuItem(category);
        testMenuItem.setId(1L);
        
        testOrder = TestDataBuilder.createTestOrder(testTable);
        testOrder.setId(1L);
        
        testOrderDTO = TestDataBuilder.createTestOrderDTO();
    }

    @Test
    void createOrder_ValidRequest_ReturnsCreatedOrder() {
        // Given
        when(tableRepository.findById(any())).thenReturn(Optional.of(testTable));
        when(menuItemRepository.findById(any())).thenReturn(Optional.of(testMenuItem));
        when(orderRepository.save(any())).thenReturn(testOrder);
        when(orderItemRepository.saveAll(any())).thenReturn(Arrays.asList(new OrderItem()));

        // When
        OrderDTO result = orderService.createOrder(testOrderDTO);

        // Then
        assertNotNull(result);
        assertEquals(testTable.getId(), result.getRestaurantTableId());
        assertEquals("Test Customer", result.getCustomerName());
        assertEquals(Order.OrderStatus.PENDING, result.getStatus());
        assertEquals(1, result.getItems().size());
        
        verify(orderRepository).save(any());
        verify(orderItemRepository).saveAll(any());
    }

    @Test
    void createOrder_InvalidTable_ThrowsException() {
        // Given
        when(tableRepository.findById(any())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> orderService.createOrder(testOrderDTO));
    }

    @Test
    void updateOrderStatus_ValidRequest_ReturnsUpdatedOrder() {
        // Given
        when(orderRepository.findById(any())).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any())).thenReturn(testOrder);

        // When
        OrderDTO result = orderService.updateOrderStatus(1L, Order.OrderStatus.IN_PROGRESS);

        // Then
        assertNotNull(result);
        assertEquals(Order.OrderStatus.IN_PROGRESS, result.getStatus());
        verify(orderRepository).save(any());
    }

    @Test
    void updateOrderStatus_InvalidOrder_ThrowsException() {
        // Given
        when(orderRepository.findById(any())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, 
            () -> orderService.updateOrderStatus(1L, Order.OrderStatus.IN_PROGRESS));
    }

    @Test
    void getOrder_ExistingOrder_ReturnsOrder() {
        // Given
        when(orderRepository.findById(any())).thenReturn(Optional.of(testOrder));

        // When
        OrderDTO result = orderService.getOrder(1L);

        // Then
        assertNotNull(result);
        assertEquals(testOrder.getId(), result.getId());
        assertEquals(testTable.getId(), result.getRestaurantTableId());
    }

    @Test
    void getOrder_NonExistingOrder_ThrowsException() {
        // Given
        when(orderRepository.findById(any())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(EntityNotFoundException.class, () -> orderService.getOrder(1L));
    }

    @Test
    void getOrdersByTable_ExistingOrders_ReturnsOrderList() {
        // Given
        when(orderRepository.findByTableId(any())).thenReturn(Arrays.asList(testOrder));

        // When
        List<OrderDTO> results = orderService.getOrdersByTable(1L);

        // Then
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(testTable.getId(), results.get(0).getRestaurantTableId());
    }

    @Test
    void getOrdersByStatus_ExistingOrders_ReturnsOrderList() {
        // Given
        when(orderRepository.findByStatus(any())).thenReturn(Arrays.asList(testOrder));

        // When
        List<OrderDTO> results = orderService.getOrdersByStatus(Order.OrderStatus.PENDING);

        // Then
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals(Order.OrderStatus.PENDING, results.get(0).getStatus());
    }
} 