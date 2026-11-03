package com.myntra.clone.cart.dto;

public record EditCartRequest(
        Long itemId,
        Long skuId,
        Integer quantity
) {}
