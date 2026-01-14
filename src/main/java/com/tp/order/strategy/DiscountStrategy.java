package com.tp.order.strategy;

import java.math.BigDecimal;

public interface DiscountStrategy {
    
    BigDecimal calculateDiscount(BigDecimal orderTotal);
    
    String getDescription();
}
