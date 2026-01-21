package com.tp.order.strategy;

import com.tp.order.entity.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompositeDiscountStrategyTest {

    @Mock
    private DiscountStrategy strategy1;

    @Mock
    private DiscountStrategy strategy2;

    @Mock
    private DiscountStrategy strategy3;

    private CompositeDiscountStrategy compositeDiscountStrategy;

    @BeforeEach
    void setup() {
        compositeDiscountStrategy =
                new CompositeDiscountStrategy(List.of(strategy1, strategy2, strategy3));
    }

    @Test
    void calculateDiscount_shouldSumApplicableStrategyDiscounts() {
        // given
        UserRole role = UserRole.USER;
        BigDecimal orderTotal = BigDecimal.valueOf(1000);

        when(strategy1.isApplicable(role, orderTotal)).thenReturn(true);
        when(strategy1.calculateDiscount(orderTotal)).thenReturn(BigDecimal.valueOf(100));

        when(strategy2.isApplicable(role, orderTotal)).thenReturn(true);
        when(strategy2.calculateDiscount(orderTotal)).thenReturn(BigDecimal.valueOf(50));

        when(strategy3.isApplicable(role, orderTotal)).thenReturn(false);

        // when
        BigDecimal discount =
                compositeDiscountStrategy.calculateDiscount(role, orderTotal);

        // then
        assertEquals(BigDecimal.valueOf(150), discount);
    }

    @Test
    void calculateDiscount_shouldReturnZeroWhenNoStrategyApplies() {
        // given
        UserRole role = UserRole.ADMIN;
        BigDecimal orderTotal = BigDecimal.valueOf(500);

        when(strategy1.isApplicable(role, orderTotal)).thenReturn(false);
        when(strategy2.isApplicable(role, orderTotal)).thenReturn(false);
        when(strategy3.isApplicable(role, orderTotal)).thenReturn(false);

        // when
        BigDecimal discount =
                compositeDiscountStrategy.calculateDiscount(role, orderTotal);

        // then
        assertEquals(BigDecimal.ZERO, discount);
    }

    @Test
    void getCombinedDescription_shouldJoinDescriptionsOfApplicableStrategies() {
        // given
        UserRole role = UserRole.USER;
        BigDecimal orderTotal = BigDecimal.valueOf(200);

        when(strategy1.isApplicable(role, orderTotal)).thenReturn(true);
        when(strategy1.getDescription()).thenReturn("Role discount");

        when(strategy2.isApplicable(role, orderTotal)).thenReturn(true);
        when(strategy2.getDescription()).thenReturn("Seasonal discount");

        when(strategy3.isApplicable(role, orderTotal)).thenReturn(false);

        // when
        String description =
                compositeDiscountStrategy.getCombinedDescription(role, orderTotal);

        // then
        assertEquals("Role discount + Seasonal discount", description);
    }

    @Test
    void getCombinedDescription_shouldReturnDefaultMessageWhenNoStrategyApplies() {
        // given
        UserRole role = UserRole.USER;
        BigDecimal orderTotal = BigDecimal.valueOf(50);

        when(strategy1.isApplicable(role, orderTotal)).thenReturn(false);
        when(strategy2.isApplicable(role, orderTotal)).thenReturn(false);
        when(strategy3.isApplicable(role, orderTotal)).thenReturn(false);

        // when
        String description =
                compositeDiscountStrategy.getCombinedDescription(role, orderTotal);

        // then
        assertEquals("No discount applied", description);
    }
}
