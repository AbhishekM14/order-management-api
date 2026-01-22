package com.tp.order.strategy;

import org.springframework.stereotype.Component;

import com.tp.order.entity.UserRole;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

@Component("premiumUserDiscountStrategy")
public class PremiumUserDiscountStrategy implements DiscountStrategy {

	private static final BigDecimal PREMIUM_DISCOUNT_RATE = new BigDecimal("0.10");

	@Override
	public boolean isApplicable(UserRole userRole, BigDecimal orderTotal) {
		return userRole == UserRole.PREMIUM_USER;
	}

	@Override
	public BigDecimal calculateDiscount(BigDecimal orderTotal) {
		Objects.requireNonNull(orderTotal, "orderTotal must not be null");

		return orderTotal
				.multiply(PREMIUM_DISCOUNT_RATE)
				.setScale(2, RoundingMode.HALF_UP);
	}

	@Override
	public String getDescription() {
		return "10% discount for premium users";
	}
}
