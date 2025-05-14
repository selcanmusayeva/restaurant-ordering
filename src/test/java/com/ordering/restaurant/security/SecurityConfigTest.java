package com.ordering.restaurant.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void publicEndpoints_ShouldBeAccessible() throws Exception {
        // Menu endpoints
        mockMvc.perform(get("/api/v1/menu"))
                .andExpect(status().isOk());
        
        mockMvc.perform(get("/api/v1/menu/items/1"))
                .andExpect(status().isOk());
        
        mockMvc.perform(get("/api/v1/menu/categories"))
                .andExpect(status().isOk());
        
        mockMvc.perform(get("/api/v1/menu/items/available"))
                .andExpect(status().isOk());

        // Auth endpoints
        mockMvc.perform(post("/api/v1/auth/register"))
                .andExpect(status().isOk());
        
        mockMvc.perform(post("/api/v1/auth/login"))
                .andExpect(status().isOk());

        // Swagger endpoints
        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk());
        
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk());
    }

    @Test
    void protectedEndpoints_ShouldRequireAuthentication() throws Exception {
        // Admin endpoints
        mockMvc.perform(get("/api/v1/admin/users"))
                .andExpect(status().isUnauthorized());
        
        mockMvc.perform(post("/api/v1/admin/users"))
                .andExpect(status().isUnauthorized());

        // Manager endpoints
        mockMvc.perform(get("/api/v1/manager/reports"))
                .andExpect(status().isUnauthorized());
        
        mockMvc.perform(post("/api/v1/manager/menu"))
                .andExpect(status().isUnauthorized());

        // Waiter endpoints
        mockMvc.perform(get("/api/v1/waiter/tables"))
                .andExpect(status().isUnauthorized());
        
        mockMvc.perform(post("/api/v1/waiter/orders"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void corsConfiguration_ShouldAllowSpecifiedOrigins() throws Exception {
        mockMvc.perform(get("/api/v1/menu")
                .header("Origin", "http://localhost:3000"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Access-Control-Allow-Origin"))
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:3000"));
    }

    @Test
    void corsConfiguration_ShouldAllowSpecifiedMethods() throws Exception {
        mockMvc.perform(options("/api/v1/menu")
                .header("Origin", "http://localhost:3000")
                .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Access-Control-Allow-Methods"));
    }

    @Test
    void corsConfiguration_ShouldAllowSpecifiedHeaders() throws Exception {
        mockMvc.perform(options("/api/v1/menu")
                .header("Origin", "http://localhost:3000")
                .header("Access-Control-Request-Headers", "Authorization,Content-Type"))
                .andExpect(status().isOk())
                .andExpect(header().exists("Access-Control-Allow-Headers"));
    }
} 