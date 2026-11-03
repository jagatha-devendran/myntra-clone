package com.myntra.clone.user.dto;

public record AddToWishlistResponse(
        String message,
        Long id,
        Long productId
) {
}
