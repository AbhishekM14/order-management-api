package com.tp.order.strategy;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.tp.order.entity.UserRole;

class UserDiscountStrategyTest {

    private UserDiscountStrategy strategy;

    @BeforeEach
    void setUp() {
        strategy = new UserDiscountStrategy();
    }

    @Test
    void isApplicable_shouldReturnTrue_forUserRoleUSER() {
        BigDecimal orderTotal = new BigDecimal("100.00");

        boolean applicable = strategy.isApplicable(UserRole.USER, orderTotal);

        assertTrue(applicable);
    }

    @Test
    void isApplicable_shouldReturnTrue_forUserRoleADMIN() {
        BigDecimal orderTotal = new BigDecimal("100.00");

        boolean applicable = strategy.isApplicable(UserRole.ADMIN, orderTotal);

        assertTrue(applicable);
    }

    @Test
    void isApplicable_shouldReturnFalse_forOtherRoles() {
        BigDecimal orderTotal = new BigDecimal("100.00");

        assertFalse(strategy.isApplicable(UserRole.USER, orderTotal));
        assertFalse(strategy.isApplicable(UserRole.ADMIN, orderTotal));
        assertFalse(strategy.isApplicable(UserRole.PREMIUM_USER, orderTotal));
    }

    @Test
    void calculateDiscount_shouldAlwaysReturnZero() {
        BigDecimal orderTotal = new BigDecimal("500.00");

        BigDecimal discount = strategy.calculateDiscount(orderTotal);

        assertEquals(BigDecimal.ZERO, discount);
    }

    @Test
    void calculateDiscount_shouldReturnZero_forZeroOrderTotal() {
        BigDecimal discount = strategy.calculateDiscount(BigDecimal.ZERO);

        assertEquals(BigDecimal.ZERO, discount);
    }

    @Test
    void getDescription_shouldReturnNoDiscount() {
        assertEquals("No discount", strategy.getDescription());
    }
}