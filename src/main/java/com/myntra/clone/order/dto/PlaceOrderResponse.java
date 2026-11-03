package com.myntra.clone.order.dto;

import java.math.BigDecimal;

public record PlaceOrderResponse(
        String orderNumber,
        String status,
        BigDecimal totalAmount,
        String message,
        String razorpayOrderId,
        String razorpayKeyId
) {
}
