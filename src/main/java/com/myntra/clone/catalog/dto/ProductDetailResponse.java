package com.myntra.clone.catalog.dto;

import java.math.BigDecimal;
import java.util.List;

public record ProductDetailResponse(
        Long productId,
        String title,
        String brand,
        String category,
        String gender,
        String primaryColour,
        BigDecimal mrp,
        BigDecimal price,
        String discountLabel,
        BigDecimal rating,
        Integer ratingCount,
        List<String> images,
        List<SkuDto> skus,
        boolean isWishlisted) {}
