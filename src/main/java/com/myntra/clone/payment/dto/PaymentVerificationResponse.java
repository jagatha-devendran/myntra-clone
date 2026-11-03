package com.myntra.clone.payment.dto;

public record PaymentVerificationResponse(
        String orderNumber,
        String orderStatus,
        String paymentStatus,
        String message
) {}