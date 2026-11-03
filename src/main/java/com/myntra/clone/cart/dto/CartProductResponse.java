package com.myntra.clone.cart.dto;

import java.math.BigDecimal;

public record CartProductResponse(
        Long itemId,
        Long productId,
        String productName,
        String brand,
        Long skuId,
        String size,
        Integer quantity,
        BigDecimal mrp,
        BigDecimal price,
        BigDecimal subtotal,
        String imageUrl
) {
}
