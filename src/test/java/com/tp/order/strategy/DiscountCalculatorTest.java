package com.tp.order.strategy;

import com.tp.order.entity.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DiscountCalculatorTest {

    @Mock
    private CompositeDiscountStrategy discountStrategy;

    @InjectMocks
    private DiscountCalculator discountCalculator;

    @Test
    void calculateDiscount_shouldDelegateToCompositeStrategy() {
        // given
        UserRole role = UserRole.USER;
        BigDecimal orderTotal = BigDecimal.valueOf(1000);
        BigDecimal expectedDiscount = BigDecimal.valueOf(150);

        when(discountStrategy.calculateDiscount(role, orderTotal))
                .thenReturn(expectedDiscount);

        // when
        BigDecimal result = discountCalculator.calculateDiscount(role, orderTotal);

        // then
        assertEquals(expectedDiscount, result);
        verify(discountStrategy).calculateDiscount(role, orderTotal);
    }

    @Test
    void getDiscountDescription_shouldDelegateToCompositeStrategy() {
        // given
        UserRole role = UserRole.ADMIN;
        BigDecimal orderTotal = BigDecimal.valueOf(500);
        String description = "Admin discount + Seasonal discount";

        when(discountStrategy.getCombinedDescription(role, orderTotal))
                .thenReturn(description);

        // when
        String result = discountCalculator.getDiscountDescription(role, orderTotal);

        // then
        assertEquals(description, result);
        verify(discountStrategy).getCombinedDescription(role, orderTotal);
    }
}
