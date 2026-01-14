package com.tp.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tp.order.dto.*;
import com.tp.order.dto.CreateOrderRequest;
import com.tp.order.dto.OrderDTO;
import com.tp.order.dto.OrderItemDTO;
import com.tp.order.dto.OrderItemRequest;
import com.tp.order.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@Import(TestSecurityConfig.class)
class OrderControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private OrderService orderService;
    
    @Test
    @WithMockUser
    void createOrder_Success() throws Exception {
        OrderItemRequest itemRequest = new OrderItemRequest(1L, 2);
        CreateOrderRequest request = new CreateOrderRequest(List.of(itemRequest));
        
        OrderItemDTO orderItem = new OrderItemDTO(1L, 1L, "Laptop", 2,
                new BigDecimal("1299.99"), new BigDecimal("0"), new BigDecimal("2599.98"));
        OrderDTO response = new OrderDTO(1L, 1L, "testuser", List.of(orderItem),
                new BigDecimal("2599.98"), "PENDING", LocalDateTime.now(), LocalDateTime.now());
        
        when(orderService.createOrder(any(CreateOrderRequest.class))).thenReturn(response);
        
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderTotal").value(2599.98))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }
    
    @Test
    @WithMockUser
    void createOrder_InvalidInput_ReturnsBadRequest() throws Exception {
        CreateOrderRequest request = new CreateOrderRequest(List.of());
        
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    @WithMockUser
    void getOrderById_Success() throws Exception {
        OrderItemDTO orderItem = new OrderItemDTO(1L, 1L, "Laptop", 2, 
                new BigDecimal("1299.99"), new BigDecimal("0"), new BigDecimal("2599.98"));
        OrderDTO order = new OrderDTO(1L, 1L, "testuser", List.of(orderItem), 
                new BigDecimal("2599.98"), "PENDING", LocalDateTime.now(), LocalDateTime.now());
        
        when(orderService.getOrderById(1L)).thenReturn(order);
        
        mockMvc.perform(get("/api/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.orderTotal").value(2599.98));
    }
    
    @Test
    @WithMockUser
    void getMyOrders_Success() throws Exception {
        OrderItemDTO orderItem = new OrderItemDTO(1L, 1L, "Laptop", 2, 
                new BigDecimal("1299.99"), new BigDecimal("0"), new BigDecimal("2599.98"));
        OrderDTO order = new OrderDTO(1L, 1L, "testuser", List.of(orderItem), 
                new BigDecimal("2599.98"), "PENDING", LocalDateTime.now(), LocalDateTime.now());
        Page<OrderDTO> page = new PageImpl<>(List.of(order), PageRequest.of(0, 10), 1);
        
        when(orderService.getMyOrders(any())).thenReturn(page);
        
        mockMvc.perform(get("/api/orders/my-orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].orderTotal").value(2599.98));
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllOrders_Success_WhenAdmin() throws Exception {
        OrderItemDTO orderItem = new OrderItemDTO(1L, 1L, "Laptop", 2, 
                new BigDecimal("1299.99"), new BigDecimal("0"), new BigDecimal("2599.98"));
        OrderDTO order = new OrderDTO(1L, 1L, "testuser", List.of(orderItem), 
                new BigDecimal("2599.98"), "PENDING", LocalDateTime.now(), LocalDateTime.now());
        Page<OrderDTO> page = new PageImpl<>(List.of(order), PageRequest.of(0, 10), 1);
        
        when(orderService.getAllOrders(any())).thenReturn(page);
        
        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].orderTotal").value(2599.98));
    }
    
    @Test
    @WithMockUser(roles = "USER")
    void getAllOrders_Forbidden_WhenNotAdmin() throws Exception {
        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isForbidden());
    }
}
