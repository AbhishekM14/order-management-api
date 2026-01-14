package com.tp.order.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderDTO(
        Long id,
        Long userId,
        String username,
        List<OrderItemDTO> items,
        BigDecimal orderTotal,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
