package com.myntra.clone.user.dto;

import java.math.BigDecimal;
import java.util.List;

public record WishListItems(
        Long wishlistId,
        Long productId,
        String title,
        String brand,
        BigDecimal price,
        BigDecimal mrp,
        String discountDisplayLabel,
        BigDecimal rating,
        Integer ratingCount,
        List<String> images

) {
}
