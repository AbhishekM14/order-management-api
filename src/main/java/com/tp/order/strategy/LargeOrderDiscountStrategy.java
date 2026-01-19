package com.tp.order.strategy;

import org.springframework.stereotype.Component;

import com.tp.order.entity.UserRole;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component("largeOrderDiscountStrategy")
public class LargeOrderDiscountStrategy implements DiscountStrategy {

	private static final BigDecimal THRESHOLD = new BigDecimal("500.00");
	private static final BigDecimal LARGE_ORDER_DISCOUNT_RATE = new BigDecimal("0.05");

	@Override
	public boolean isApplicable(UserRole userRole, BigDecimal orderTotal) {
		return orderTotal.compareTo(THRESHOLD) > 0;
	}

	@Override
	public BigDecimal calculateDiscount(BigDecimal orderTotal) {
		if (orderTotal.compareTo(THRESHOLD) > 0) {
			return orderTotal.multiply(LARGE_ORDER_DISCOUNT_RATE).setScale(2, RoundingMode.HALF_UP);
		}
		return BigDecimal.ZERO;
	}

	@Override
	public String getDescription() {
		return "5% discount for orders above $500";
	}
}
