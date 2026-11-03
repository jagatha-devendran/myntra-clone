package com.myntra.clone.payment.service;

import com.myntra.clone.payment.dto.RazorpayOrderResult;

import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Order;

import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class RazorpayService {

    private final RazorpayClient razorpayClient;

    @Value("${razorpay.key.id}")
    private String keyId;

    public RazorpayService(RazorpayClient razorpayClient) {
        this.razorpayClient = razorpayClient;
    }

    public RazorpayOrderResult createOrder(
            BigDecimal amount,
            String receipt) {

        try {

            // Convert rupees into paise
            long amountInPaise = amount
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(0, RoundingMode.UNNECESSARY)
                    .longValueExact();

            JSONObject orderRequest = new JSONObject();

            orderRequest.put("amount", amountInPaise);
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", receipt);

            Order razorpayOrder =
                    razorpayClient.orders.create(orderRequest);

            String razorpayOrderId =
                    razorpayOrder.get("id").toString();

            return new RazorpayOrderResult(
                    razorpayOrderId,
                    keyId
            );

        } catch (RazorpayException e) {

            throw new RuntimeException(
                    "Failed to create Razorpay order: " + e.getMessage()
            );
        }
    }
}