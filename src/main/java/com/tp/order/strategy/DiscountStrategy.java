package com.tp.order.strategy;

import java.math.BigDecimal;

import com.tp.order.entity.UserRole;

public interface DiscountStrategy {
    
    BigDecimal calculateDiscount(BigDecimal orderTotal);
    boolean isApplicable(UserRole userRole, BigDecimal orderTotal);
    String getDescription();
}
