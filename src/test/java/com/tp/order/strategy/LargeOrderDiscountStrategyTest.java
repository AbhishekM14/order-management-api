package com.tp.order.strategy;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.tp.order.entity.UserRole;

class LargeOrderDiscountStrategyTest {

    private LargeOrderDiscountStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new LargeOrderDiscountStrategy();
    }

    @Test
    void isApplicable_shouldReturnTrue_whenOrderTotalIsAboveThreshold() {
        BigDecimal orderTotal = new BigDecimal("500.00");

        boolean applicable = strategy.isApplicable(UserRole.USER, orderTotal);

        assertTrue(applicable);
    }

    @Test
    void isApplicable_shouldReturnFalse_whenOrderTotalIsEqualToThreshold() {
        BigDecimal orderTotal = new BigDecimal("500.00");

        boolean applicable = strategy.isApplicable(UserRole.USER, orderTotal);

        assertFalse(applicable);
    }

    @Test
    void isApplicable_shouldReturnFalse_whenOrderTotalIsBelowThreshold() {
        BigDecimal orderTotal = new BigDecimal("499.99");

        boolean applicable = strategy.isApplicable(UserRole.USER, orderTotal);

        assertFalse(applicable);
    }

    @Test
    void calculateDiscount_shouldReturnFivePercent_whenAboveThreshold() {
        BigDecimal orderTotal = new BigDecimal("500.00");

        BigDecimal discount = strategy.calculateDiscount(orderTotal);

        assertEquals(new BigDecimal("30.00"), discount);
    }

    @Test
    void calculateDiscount_shouldReturnZero_whenEqualToThreshold() {
        BigDecimal orderTotal = new BigDecimal("500.00");

        BigDecimal discount = strategy.calculateDiscount(orderTotal);

        assertEquals(BigDecimal.ZERO, discount);
    }

    @Test
    void calculateDiscount_shouldReturnZero_whenBelowThreshold() {
        BigDecimal orderTotal = new BigDecimal("400.00");

        BigDecimal discount = strategy.calculateDiscount(orderTotal);

        assertEquals(BigDecimal.ZERO, discount);
    }

    @Test
    void calculateDiscount_shouldRoundToTwoDecimalPlaces() {
        BigDecimal orderTotal = new BigDecimal("555.55");

        BigDecimal discount = strategy.calculateDiscount(orderTotal);

        // 555.55 * 0.05 = 27.7775 → 27.78
        assertEquals(new BigDecimal("27.78"), discount);
    }

    @Test
    void getDescription_shouldReturnCorrectDescription() {
        assertEquals("5% discount for orders above $500", strategy.getDescription());
    }
}