package com.myntra.clone.cart.dto;

import java.math.BigDecimal;

public record CartActionResponse(
        boolean success,
        String message,
        int cartItemCount,
        BigDecimal cartTotal
) {}
