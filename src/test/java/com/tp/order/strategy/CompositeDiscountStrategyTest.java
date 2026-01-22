package com.tp.order.strategy;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

	private CompositeDiscountStrategy compositeStrategy;

	@BeforeEach
	void setUp() {
		compositeStrategy = new CompositeDiscountStrategy(
				List.of(strategy1, strategy2)
		);
	}

	@Test
	void shouldSumDiscountsFromApplicableStrategies() {
		BigDecimal orderTotal = new BigDecimal("1000.00");

		when(strategy1.isApplicable(UserRole.PREMIUM_USER, orderTotal)).thenReturn(true);
		when(strategy2.isApplicable(UserRole.PREMIUM_USER, orderTotal)).thenReturn(true);

		when(strategy1.calculateDiscount(orderTotal)).thenReturn(new BigDecimal("50.00"));
		when(strategy2.calculateDiscount(orderTotal)).thenReturn(new BigDecimal("100.00"));

		BigDecimal discount = compositeStrategy.calculateDiscount(
				UserRole.PREMIUM_USER, orderTotal);

		assertEquals(new BigDecimal("150.00"), discount);
	}

	@Test
	void shouldReturnZeroWhenNoStrategyIsApplicable() {
		BigDecimal orderTotal = new BigDecimal("500.00");

		when(strategy1.isApplicable(any(), any())).thenReturn(false);
		when(strategy2.isApplicable(any(), any())).thenReturn(false);

		BigDecimal discount = compositeStrategy.calculateDiscount(
				UserRole.USER, orderTotal);

		assertEquals(BigDecimal.ZERO, discount);
	}

	@Test
	void shouldReturnZeroWhenOrderTotalIsNull() {
		BigDecimal discount = compositeStrategy.calculateDiscount(
				UserRole.PREMIUM_USER, null);

		assertEquals(BigDecimal.ZERO, discount);
	}

	@Test
	void shouldReturnZeroWhenOrderTotalIsZeroOrNegative() {
		BigDecimal discount = compositeStrategy.calculateDiscount(
				UserRole.PREMIUM_USER, BigDecimal.ZERO);

		assertEquals(BigDecimal.ZERO, discount);
	}

	@Test
	void shouldCombineDescriptionsCorrectly() {
		BigDecimal orderTotal = new BigDecimal("800.00");

		when(strategy1.isApplicable(UserRole.PREMIUM_USER, orderTotal)).thenReturn(true);
		when(strategy2.isApplicable(UserRole.PREMIUM_USER, orderTotal)).thenReturn(true);

		when(strategy1.getDescription()).thenReturn("5% large order discount");
		when(strategy2.getDescription()).thenReturn("10% premium user discount");

		String description = compositeStrategy.getCombinedDescription(
				UserRole.PREMIUM_USER, orderTotal);

		assertEquals(
				"5% large order discount + 10% premium user discount",
				description
		);
	}

	@Test
	void shouldReturnDefaultDescriptionWhenNoDiscountApplied() {
		BigDecimal orderTotal = new BigDecimal("200.00");

		when(strategy1.isApplicable(any(), any())).thenReturn(false);
		when(strategy2.isApplicable(any(), any())).thenReturn(false);

		String description = compositeStrategy.getCombinedDescription(
				UserRole.USER, orderTotal);

		assertEquals("No discount applied", description);
	}
}
