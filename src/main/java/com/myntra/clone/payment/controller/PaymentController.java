package com.myntra.clone.payment.controller;

import com.myntra.clone.payment.dto.PaymentVerificationRequest;
import com.myntra.clone.payment.dto.PaymentVerificationResponse;
import com.myntra.clone.payment.service.PaymentService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/verify")
    public ResponseEntity<PaymentVerificationResponse> verifyPayment(
            @RequestBody PaymentVerificationRequest request,
            Authentication authentication) {

        String email = authentication.getName();

        PaymentVerificationResponse response =
                paymentService.verifyPayment(email, request);

        return ResponseEntity.ok(response);
    }
}