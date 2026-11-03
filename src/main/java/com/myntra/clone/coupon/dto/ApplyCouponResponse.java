package com.myntra.clone.coupon.dto;

import java.math.BigDecimal;

public record ApplyCouponResponse(
        boolean success,
        String message,
        String code,
        String discountType,
        BigDecimal discountValue,
        BigDecimal minOrderValue,
        BigDecimal discountAmount,
        BigDecimal cartTotal,
        BigDecimal finalAmount
) {}
