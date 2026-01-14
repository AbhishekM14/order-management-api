package com.tp.order.strategy;

import com.tp.order.entity.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DiscountCalculator {
    
    private final Map<String, DiscountStrategy> discountStrategies;
    
    public BigDecimal calculateDiscount(UserRole userRole, BigDecimal orderTotal) {
        DiscountStrategy baseStrategy = getBaseStrategy(userRole);
        DiscountStrategy largeOrderStrategy = discountStrategies.get("largeOrderDiscountStrategy");
        
        BigDecimal baseDiscount = baseStrategy.calculateDiscount(orderTotal);
        BigDecimal largeOrderDiscount = largeOrderStrategy.calculateDiscount(orderTotal);
        
        return baseDiscount.add(largeOrderDiscount);
    }
    
    private DiscountStrategy getBaseStrategy(UserRole userRole) {
        return switch (userRole) {
            case PREMIUM_USER -> discountStrategies.get("premiumUserDiscountStrategy");
            case USER, ADMIN -> discountStrategies.get("userDiscountStrategy");
        };
    }
    
    public String getDiscountDescription(UserRole userRole, BigDecimal orderTotal) {
        StringBuilder description = new StringBuilder();
        
        if (userRole == UserRole.PREMIUM_USER) {
            description.append("10% premium user discount");
        }
        
        if (orderTotal.compareTo(new BigDecimal("500.00")) > 0) {
            if (description.length() > 0) {
                description.append(" + ");
            }
            description.append("5% large order discount");
        }
        
        if (description.length() == 0) {
            description.append("No discount applied");
        }
        
        return description.toString();
    }
}
