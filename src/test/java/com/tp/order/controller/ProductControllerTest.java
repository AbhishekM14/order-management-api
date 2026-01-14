package com.tp.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tp.order.dto.ProductDTO;
import com.tp.order.service.ProductService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@Import(TestSecurityConfig.class)
class ProductControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @MockBean
    private ProductService productService;
    
    @Test
    @WithMockUser
    void getAllProducts_Success() throws Exception {
        ProductDTO product = new ProductDTO(1L, "Laptop", "Gaming laptop", 
                new BigDecimal("1299.99"), 50, false, LocalDateTime.now(), LocalDateTime.now());
        Page<ProductDTO> page = new PageImpl<>(List.of(product), PageRequest.of(0, 10), 1);
        
        when(productService.getAllProducts(any())).thenReturn(page);
        
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Laptop"))
                .andExpect(jsonPath("$.content[0].price").value(1299.99));
    }
    
    @Test
    @WithMockUser
    void getProductById_Success() throws Exception {
        ProductDTO product = new ProductDTO(1L, "Laptop", "Gaming laptop", 
                new BigDecimal("1299.99"), 50, false, LocalDateTime.now(), LocalDateTime.now());
        
        when(productService.getProductById(1L)).thenReturn(product);
        
        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Laptop"))
                .andExpect(jsonPath("$.price").value(1299.99));
    }
    
    @Test
    @WithMockUser
    void searchProducts_Success() throws Exception {
        ProductDTO product = new ProductDTO(1L, "Laptop", "Gaming laptop", 
                new BigDecimal("1299.99"), 50, false, LocalDateTime.now(), LocalDateTime.now());
        Page<ProductDTO> page = new PageImpl<>(List.of(product), PageRequest.of(0, 10), 1);
        
        when(productService.searchProducts(any(), any(), any(), any(), any())).thenReturn(page);
        
        mockMvc.perform(get("/api/products/search")
                .param("name", "laptop")
                .param("minPrice", "1000")
                .param("maxPrice", "2000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Laptop"));
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void createProduct_Success() throws Exception {
        ProductDTO request = new ProductDTO(null, "New Product", "Description", 
                new BigDecimal("99.99"), 100, false, null, null);
        ProductDTO response = new ProductDTO(1L, "New Product", "Description", 
                new BigDecimal("99.99"), 100, false, LocalDateTime.now(), LocalDateTime.now());
        
        when(productService.createProduct(any(ProductDTO.class))).thenReturn(response);
        
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("New Product"));
    }
    
    @Test
    @WithMockUser(roles = "USER")
    void createProduct_Forbidden_WhenNotAdmin() throws Exception {
        ProductDTO request = new ProductDTO(null, "New Product", "Description", 
                new BigDecimal("99.99"), 100, false, null, null);
        
        mockMvc.perform(post("/api/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void updateProduct_Success() throws Exception {
        ProductDTO request = new ProductDTO(1L, "Updated Product", "Updated Description", 
                new BigDecimal("149.99"), 50, false, null, null);
        ProductDTO response = new ProductDTO(1L, "Updated Product", "Updated Description", 
                new BigDecimal("149.99"), 50, false, LocalDateTime.now(), LocalDateTime.now());
        
        when(productService.updateProduct(eq(1L), any(ProductDTO.class))).thenReturn(response);
        
        mockMvc.perform(put("/api/products/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Product"));
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteProduct_Success() throws Exception {
        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());
    }
}
