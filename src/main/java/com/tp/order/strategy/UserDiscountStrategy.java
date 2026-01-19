package com.tp.order.strategy;

import org.springframework.stereotype.Component;

import com.tp.order.entity.UserRole;

import java.math.BigDecimal;

@Component("userDiscountStrategy")
public class UserDiscountStrategy implements DiscountStrategy {

	@Override
	public boolean isApplicable(UserRole userRole, BigDecimal orderTotal) {
		return userRole == UserRole.USER || userRole == UserRole.ADMIN;
	}

	@Override
	public BigDecimal calculateDiscount(BigDecimal orderTotal) {
		return BigDecimal.ZERO;
	}

	@Override
	public String getDescription() {
		return "No discount";
	}
}
