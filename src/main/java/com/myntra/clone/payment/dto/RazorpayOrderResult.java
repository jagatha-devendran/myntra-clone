package com.myntra.clone.payment.dto;

public record RazorpayOrderResult(
        String orderId,
        String keyId
) {
}
