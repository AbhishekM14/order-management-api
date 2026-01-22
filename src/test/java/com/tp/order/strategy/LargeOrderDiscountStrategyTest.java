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
	void shouldBeApplicableWhenOrderTotalIsGreaterThanThreshold() {
		BigDecimal orderTotal = new BigDecimal("600.00");

		boolean applicable = strategy.isApplicable(UserRole.USER, orderTotal);

		assertTrue(applicable);
	}

	@Test
	void shouldNotBeApplicableWhenOrderTotalIsEqualToThreshold() {
		BigDecimal orderTotal = new BigDecimal("500.00");

		boolean applicable = strategy.isApplicable(UserRole.USER, orderTotal);

		assertFalse(applicable);
	}

	@Test
	void shouldNotBeApplicableWhenOrderTotalIsBelowThreshold() {
		BigDecimal orderTotal = new BigDecimal("300.00");

		boolean applicable = strategy.isApplicable(UserRole.USER, orderTotal);

		assertFalse(applicable);
	}

	@Test
	void shouldNotBeApplicableWhenOrderTotalIsNull() {
		boolean applicable = strategy.isApplicable(UserRole.USER, null);

		assertFalse(applicable);
	}

	@Test
	void shouldCalculateCorrectDiscount() {
		BigDecimal orderTotal = new BigDecimal("1000.00");

		BigDecimal discount = strategy.calculateDiscount(orderTotal);

		assertEquals(new BigDecimal("50.00"), discount);
	}

	@Test
	void shouldCalculateDiscountWithCorrectRounding() {
		BigDecimal orderTotal = new BigDecimal("999.99");

		BigDecimal discount = strategy.calculateDiscount(orderTotal);

		assertEquals(new BigDecimal("50.00"), discount); // 49.9995 → 50.00
	}

	@Test
	void shouldReturnCorrectDescription() {
		assertEquals(
				"5% discount for orders above $500",
				strategy.getDescription()
		);
	}
}
