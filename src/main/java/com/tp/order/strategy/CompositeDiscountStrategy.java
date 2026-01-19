package com.tp.order.strategy;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Component;

import com.tp.order.entity.UserRole;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CompositeDiscountStrategy implements DiscountStrategy {

	private final List<DiscountStrategy> strategies;

	@Override
	public boolean isApplicable(UserRole userRole, BigDecimal orderTotal) {
		return true; // composite is always applicable
	}

	@Override
	public BigDecimal calculateDiscount(BigDecimal orderTotal) {
		throw new UnsupportedOperationException("Use calculateDiscount(UserRole, BigDecimal)");
	}

	public BigDecimal calculateDiscount(UserRole userRole, BigDecimal orderTotal) {
		return strategies.stream().filter(s -> s.isApplicable(userRole, orderTotal))
				.map(s -> s.calculateDiscount(orderTotal)).reduce(BigDecimal.ZERO, BigDecimal::add);
	}

	public String getCombinedDescription(UserRole userRole, BigDecimal orderTotal) {
		return strategies.stream().filter(s -> s.isApplicable(userRole, orderTotal))
				.map(DiscountStrategy::getDescription).reduce((a, b) -> a + " + " + b).orElse("No discount applied");
	}

	@Override
	public String getDescription() {
		return "Composite discount";
	}

}
