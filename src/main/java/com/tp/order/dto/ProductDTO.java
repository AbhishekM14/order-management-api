package com.tp.order.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductDTO(
        Long id,
        
        @NotBlank(message = "Product name is required")
        @Size(max = 255, message = "Product name must not exceed 255 characters")
        String name,
        
        @Size(max = 5000, message = "Description must not exceed 5000 characters")
        String description,
        
        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.01", message = "Price must be greater than 0")
        @Digits(integer = 8, fraction = 2, message = "Price format is invalid")
        BigDecimal price,
        
        @NotNull(message = "Quantity is required")
        @Min(value = 0, message = "Quantity cannot be negative")
        Integer quantity,
        
        Boolean deleted,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    // Compact constructor for creating new products (without id and timestamps)
    public ProductDTO(String name, String description, BigDecimal price, Integer quantity) {
        this(null, name, description, price, quantity, false, null, null);
    }
}
