package com.tp.order.strategy;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component("userDiscountStrategy")
public class UserDiscountStrategy implements DiscountStrategy {
    
    @Override
    public BigDecimal calculateDiscount(BigDecimal orderTotal) {
        return BigDecimal.ZERO;
    }
    
    @Override
    public String getDescription() {
        return "No discount";
    }
}
