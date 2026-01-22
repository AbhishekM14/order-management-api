package com.tp.order.strategy;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Component;

import com.tp.order.entity.UserRole;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CompositeDiscountStrategy implements DiscountStrategy {

	private final List<DiscountStrategy> strategies;

	@Override
	public boolean isApplicable(UserRole userRole, BigDecimal orderTotal) {
		// Composite itself is always applicable
		return true;
	}

	@Override
	public BigDecimal calculateDiscount(BigDecimal orderTotal) {
		throw new UnsupportedOperationException(
				"Use calculateDiscount(UserRole, BigDecimal) for composite discount calculation");
	}

	public BigDecimal calculateDiscount(UserRole userRole, BigDecimal orderTotal) {
		if (Objects.isNull(orderTotal) || orderTotal.compareTo(BigDecimal.ZERO) <= 0) {
			return BigDecimal.ZERO;
		}

		return strategies.stream()
				.filter(strategy -> strategy != this)
				.filter(strategy -> strategy.isApplicable(userRole, orderTotal))
				.map(strategy -> strategy.calculateDiscount(orderTotal))
				.reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	public String getCombinedDescription(UserRole userRole, BigDecimal orderTotal) {
		return strategies.stream()
				.filter(strategy -> strategy != this)
				.filter(strategy -> strategy.isApplicable(userRole, orderTotal))
				.map(DiscountStrategy::getDescription)
				.reduce((a, b) -> a + " + " + b)
				.orElse("No discount applied");
	}

	@Override
	public String getDescription() {
		return "Composite discount";
	}
}
