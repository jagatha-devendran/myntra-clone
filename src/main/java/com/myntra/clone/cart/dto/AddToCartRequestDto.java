package com.myntra.clone.cart.dto;

public record AddToCartRequestDto(
        Long skuId,
        int quantity
) {
}
