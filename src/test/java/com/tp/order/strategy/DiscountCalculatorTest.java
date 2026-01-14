package com.tp.order.strategy;

import com.tp.order.entity.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DiscountCalculatorTest {
    
    private DiscountCalculator discountCalculator;
    
    @BeforeEach
    void setUp() {
        Map<String, DiscountStrategy> strategies = new HashMap<>();
        strategies.put("userDiscountStrategy", new UserDiscountStrategy());
        strategies.put("premiumUserDiscountStrategy", new PremiumUserDiscountStrategy());
        strategies.put("largeOrderDiscountStrategy", new LargeOrderDiscountStrategy());
        
        discountCalculator = new DiscountCalculator(strategies);
    }
    
    @Test
    void testUserDiscount_NoDiscount() {
        // Regular user, order under $500
        BigDecimal orderTotal = new BigDecimal("300.00");
        BigDecimal discount = discountCalculator.calculateDiscount(UserRole.USER, orderTotal);
        
        assertEquals(new BigDecimal("0.00"), discount);
    }
    
    @Test
    void testPremiumUserDiscount() {
        // Premium user, order under $500
        BigDecimal orderTotal = new BigDecimal("300.00");
        BigDecimal discount = discountCalculator.calculateDiscount(UserRole.PREMIUM_USER, orderTotal);
        
        // 10% of 300 = 30
        assertEquals(new BigDecimal("30.00"), discount);
    }
    
    @Test
    void testLargeOrderDiscount_User() {
        // Regular user, order above $500
        BigDecimal orderTotal = new BigDecimal("600.00");
        BigDecimal discount = discountCalculator.calculateDiscount(UserRole.USER, orderTotal);
        
        // 5% of 600 = 30
        assertEquals(new BigDecimal("30.00"), discount);
    }
    
    @Test
    void testCombinedDiscount_PremiumUserLargeOrder() {
        // Premium user, order above $500
        BigDecimal orderTotal = new BigDecimal("600.00");
        BigDecimal discount = discountCalculator.calculateDiscount(UserRole.PREMIUM_USER, orderTotal);
        
        // 10% of 600 = 60, plus 5% of 600 = 30, total = 90
        assertEquals(new BigDecimal("90.00"), discount);
    }
    
    @Test
    void testAdminDiscount() {
        // Admin, order under $500
        BigDecimal orderTotal = new BigDecimal("300.00");
        BigDecimal discount = discountCalculator.calculateDiscount(UserRole.ADMIN, orderTotal);
        
        assertEquals(new BigDecimal("0.00"), discount);
    }
}
