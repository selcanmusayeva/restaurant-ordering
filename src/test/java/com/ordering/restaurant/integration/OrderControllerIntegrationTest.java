package com.ordering.restaurant.integration;

import com.ordering.restaurant.dto.OrderDTO;
import com.ordering.restaurant.model.*;
import com.ordering.restaurant.repository.*;
import com.ordering.restaurant.util.TestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

public class OrderControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TableRepository tableRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private RestaurantTable testTable;
    private MenuItem testMenuItem;
    private Order testOrder;

    @BeforeEach
    void setUp() {
        // Clean up repositories
        orderRepository.deleteAll();
        tableRepository.deleteAll();
        menuItemRepository.deleteAll();
        categoryRepository.deleteAll();

        // Create test data
        testTable = tableRepository.save(TestDataBuilder.createTestTable());
        Category category = categoryRepository.save(TestDataBuilder.createTestCategory());
        testMenuItem = menuItemRepository.save(TestDataBuilder.createTestMenuItem(category));
        testOrder = orderRepository.save(TestDataBuilder.createTestOrder(testTable));
    }

    @Test
    @WithMockUser(roles = "WAITER")
    void createOrder_ValidRequest_ReturnsCreatedOrder() throws Exception {
        // Given
        OrderDTO orderDTO = TestDataBuilder.createTestOrderDTO();
        orderDTO.setRestaurantTableId(testTable.getId());

        // When
        ResultActions response = mockMvc.perform(post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(orderDTO)));

        // Then
        response.andExpect(status().isCreated())
                .andExpect(jsonPath("$.restaurantTableId", is(testTable.getId().intValue())))
                .andExpect(jsonPath("$.customerName", is("Test Customer")))
                .andExpect(jsonPath("$.status", is("PENDING")))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].quantity", is(2)));
    }

    @Test
    @WithMockUser(roles = "WAITER")
    void getOrder_ExistingOrder_ReturnsOrder() throws Exception {
        // When
        ResultActions response = mockMvc.perform(get("/api/v1/orders/{id}", testOrder.getId()));

        // Then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testOrder.getId().intValue())))
                .andExpect(jsonPath("$.restaurantTableId", is(testTable.getId().intValue())))
                .andExpect(jsonPath("$.status", is("PENDING")));
    }

    @Test
    @WithMockUser(roles = "WAITER")
    void getOrder_NonExistingOrder_ReturnsNotFound() throws Exception {
        // When
        ResultActions response = mockMvc.perform(get("/api/v1/orders/{id}", 999L));

        // Then
        response.andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "KITCHEN")
    void updateOrderStatus_ValidStatus_ReturnsUpdatedOrder() throws Exception {
        // When
        ResultActions response = mockMvc.perform(put("/api/v1/orders/{id}/status", testOrder.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content("\"PREPARING\""));

        // Then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testOrder.getId().intValue())))
                .andExpect(jsonPath("$.status", is("PREPARING")));
    }

    @Test
    @WithMockUser(roles = "WAITER")
    void getOrdersByTable_ExistingOrders_ReturnsOrderList() throws Exception {
        // When
        ResultActions response = mockMvc.perform(get("/api/v1/orders/tables/{tableId}", testTable.getId()));

        // Then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].restaurantTableId", is(testTable.getId().intValue())));
    }

    @Test
    @WithMockUser(roles = "KITCHEN")
    void getOrdersByStatus_ExistingOrders_ReturnsOrderList() throws Exception {
        // When
        ResultActions response = mockMvc.perform(get("/api/v1/orders/status/PENDING"));

        // Then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].status", is("PENDING")));
    }
} 