package com.tp.order.strategy;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.tp.order.entity.UserRole;

class PremiumUserDiscountStrategyTest {

    private PremiumUserDiscountStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new PremiumUserDiscountStrategy();
    }

    @Test
    void isApplicable_shouldReturnTrue_forPremiumUser() {
        BigDecimal orderTotal = new BigDecimal("200.00");

        boolean applicable = strategy.isApplicable(UserRole.PREMIUM_USER, orderTotal);

        assertTrue(applicable);
    }

    @Test
    void isApplicable_shouldReturnFalse_forNonPremiumUser() {
        BigDecimal orderTotal = new BigDecimal("200.00");

        boolean applicable = strategy.isApplicable(UserRole.PREMIUM_USER, orderTotal);

        assertFalse(applicable);
    }

    @Test
    void calculateDiscount_shouldReturnTenPercentOfOrderTotal() {
        BigDecimal orderTotal = new BigDecimal("200.00");

        BigDecimal discount = strategy.calculateDiscount(orderTotal);

        assertEquals(new BigDecimal("20.00"), discount);
    }

    @Test
    void calculateDiscount_shouldRoundToTwoDecimalPlaces() {
        BigDecimal orderTotal = new BigDecimal("333.33");

        BigDecimal discount = strategy.calculateDiscount(orderTotal);

        // 333.33 * 0.10 = 33.333 → 33.33 (HALF_UP)
        assertEquals(new BigDecimal("33.33"), discount);
    }

    @Test
    void calculateDiscount_shouldReturnZero_whenOrderTotalIsZero() {
        BigDecimal orderTotal = BigDecimal.ZERO;

        BigDecimal discount = strategy.calculateDiscount(orderTotal);

        assertEquals(BigDecimal.ZERO.setScale(2), discount);
    }

    @Test
    void getDescription_shouldReturnCorrectDescription() {
        assertEquals("10% premium user discount", strategy.getDescription());
    }
}