package com.myntra.clone.coupon.dto;

import java.math.BigDecimal;

public record CouponDto(
        Long id,
        String code,
        String discountType,
        BigDecimal discountValue,
        BigDecimal minOrderValue,
        Boolean isActive,
        String title,
        String description
) {}
