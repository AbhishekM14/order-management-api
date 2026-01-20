package com.tp.order.strategy;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tp.order.entity.UserRole;

@ExtendWith(MockitoExtension.class)
class CompositeDiscountStrategyTest {

    @Mock
    private DiscountStrategy strategy1;

    @Mock
    private DiscountStrategy strategy2;

    @Mock
    private DiscountStrategy strategy3;

    private CompositeDiscountStrategy compositeDiscountStrategy;

    private final BigDecimal orderTotal = new BigDecimal("100.00");
    private final UserRole userRole = UserRole.ADMIN;

    @BeforeEach
    void setUp() {
        compositeDiscountStrategy =
                new CompositeDiscountStrategy();
    }

    @Test
    void isApplicable_shouldAlwaysReturnTrue() {
        assertTrue(compositeDiscountStrategy.isApplicable(userRole, orderTotal));
    }

    @Test
    void calculateDiscount_shouldSumOnlyApplicableStrategies() {
        when(strategy1.isApplicable(userRole, orderTotal)).thenReturn(true);
        when(strategy1.calculateDiscount(orderTotal)).thenReturn(new BigDecimal("10.00"));

        when(strategy2.isApplicable(userRole, orderTotal)).thenReturn(true);
        when(strategy2.calculateDiscount(orderTotal)).thenReturn(new BigDecimal("5.00"));

        when(strategy3.isApplicable(userRole, orderTotal)).thenReturn(false);

        BigDecimal discount =
                compositeDiscountStrategy.calculateDiscount(userRole, orderTotal);

        assertEquals(new BigDecimal("15.00"), discount);
    }

    @Test
    void calculateDiscount_shouldReturnZeroWhenNoStrategyIsApplicable() {
        when(strategy1.isApplicable(userRole, orderTotal)).thenReturn(false);
        when(strategy2.isApplicable(userRole, orderTotal)).thenReturn(false);
        when(strategy3.isApplicable(userRole, orderTotal)).thenReturn(false);

        BigDecimal discount =
                compositeDiscountStrategy.calculateDiscount(userRole, orderTotal);

        assertEquals(BigDecimal.ZERO, discount);
    }

    @Test
    void getCombinedDescription_shouldCombineDescriptionsOfApplicableStrategies() {
        when(strategy1.isApplicable(userRole, orderTotal)).thenReturn(true);
        when(strategy1.getDescription()).thenReturn("Role discount");

        when(strategy2.isApplicable(userRole, orderTotal)).thenReturn(true);
        when(strategy2.getDescription()).thenReturn("Seasonal discount");

        when(strategy3.isApplicable(userRole, orderTotal)).thenReturn(false);

        String description =
                compositeDiscountStrategy.getCombinedDescription(userRole, orderTotal);

        assertEquals("Role discount + Seasonal discount", description);
    }

    @Test
    void getCombinedDescription_shouldReturnDefaultMessageWhenNoDiscountApplied() {
        when(strategy1.isApplicable(userRole, orderTotal)).thenReturn(false);
        when(strategy2.isApplicable(userRole, orderTotal)).thenReturn(false);
        when(strategy3.isApplicable(userRole, orderTotal)).thenReturn(false);

        String description =
                compositeDiscountStrategy.getCombinedDescription(userRole, orderTotal);

        assertEquals("No discount applied", description);
    }

    @Test
    void calculateDiscount_withoutUserRole_shouldThrowException() {
        assertThrows(UnsupportedOperationException.class,
                () -> compositeDiscountStrategy.calculateDiscount(orderTotal));
    }
}
