package com.tp.order.strategy;

import com.tp.order.entity.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class DiscountCalculator {

	private final CompositeDiscountStrategy discountStrategy;

	public BigDecimal calculateDiscount(UserRole userRole, BigDecimal orderTotal) {
		return discountStrategy.calculateDiscount(userRole, orderTotal);
	}

	public String getDiscountDescription(UserRole userRole, BigDecimal orderTotal) {
		return discountStrategy.getCombinedDescription(userRole, orderTotal);
	}
}
