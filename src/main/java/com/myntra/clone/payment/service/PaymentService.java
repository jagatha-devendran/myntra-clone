package com.myntra.clone.payment.service;

import com.myntra.clone.cart.repository.CartRepository;
import com.myntra.clone.catalog.repository.ProductSkuRepository;
import com.myntra.clone.payment.dto.PaymentVerificationRequest;
import com.myntra.clone.payment.dto.PaymentVerificationResponse;

import com.myntra.clone.order.entity.Order;
import com.myntra.clone.order.dto.OrderStatus;
import com.myntra.clone.order.dto.PaymentMethod;
import com.myntra.clone.order.dto.PaymentStatus;
import com.myntra.clone.order.repository.OrderRepository;

import com.myntra.clone.user.entity.Users;
import com.myntra.clone.user.repository.AuthRepository;


import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Payment;
import com.razorpay.Utils;

import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class PaymentService {

    private final OrderRepository orderRepository;
    private final AuthRepository authRepository;
    private final CartRepository cartItemsRepository;
    private final RazorpayClient razorpayClient;
    private final ProductSkuRepository productSkuRepository;

    @Value("${razorpay.key.secret}")
    private String keySecret;

    public PaymentService(
            OrderRepository orderRepository,
            AuthRepository authRepository,
            CartRepository cartItemsRepository,
            RazorpayClient razorpayClient,
            ProductSkuRepository productSkuRepository) {

        this.orderRepository = orderRepository;
        this.authRepository = authRepository;
        this.cartItemsRepository = cartItemsRepository;
        this.razorpayClient = razorpayClient;
        this.productSkuRepository = productSkuRepository;
    }

    @Transactional
    public PaymentVerificationResponse verifyPayment(
            String email,
            PaymentVerificationRequest request) {

        // 1. Get logged-in user
        Users user = authRepository.findByEmail(email);

        if (user == null) {
            throw new RuntimeException("User not found");
        }

        // 2. Find the user's order using Razorpay order ID
        Order order = orderRepository
                .findByGatewayOrderIdAndUserId(
                        request.razorpayOrderId(),
                        user.getId()
                )
                .orElseThrow(() ->
                        new RuntimeException("Order not found"));

        // 3. Ensure this is an online payment
        if (order.getPaymentMethod() != PaymentMethod.ONLINE) {
            throw new RuntimeException("This is not an online payment");
        }

        // 4. Handle repeated verification requests
        if (order.getPaymentStatus() == PaymentStatus.PAID) {

            if (request.razorpayPaymentId()
                    .equals(order.getGatewayPaymentId())) {

                return new PaymentVerificationResponse(
                        order.getOrderNumber(),
                        order.getStatus().name(),
                        order.getPaymentStatus().name(),
                        "Payment already verified"
                );
            }

            throw new RuntimeException("Order is already paid");
        }

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new RuntimeException("Order cannot be paid");
        }

        try {

            JSONObject options = new JSONObject();

            options.put(
                    "razorpay_order_id",
                    order.getGatewayOrderId()
            );

            options.put(
                    "razorpay_payment_id",
                    request.razorpayPaymentId()
            );

            options.put(
                    "razorpay_signature",
                    request.razorpaySignature()
            );

            boolean isValid =
                    Utils.verifyPaymentSignature(options, keySecret);

            if (!isValid) {
                throw new RuntimeException(
                        "Invalid Razorpay payment signature"
                );
            }

            Payment payment = razorpayClient.payments.fetch(
                    request.razorpayPaymentId()
            );

            String paymentStatus = payment.get("status").toString();

            if (!"captured".equals(paymentStatus)) {
                throw new RuntimeException(
                        "Payment has not been captured yet"
                );
            }

            String paymentOrderId =
                    payment.get("order_id").toString();

            if (!order.getGatewayOrderId().equals(paymentOrderId)) {
                throw new RuntimeException(
                        "Payment does not belong to this order"
                );
            }

            long paidAmount =
                    ((Number) payment.get("amount")).longValue();

            long expectedAmount = order.getTotalAmount()
                    .multiply(BigDecimal.valueOf(100))
                    .longValueExact();

            if (paidAmount != expectedAmount) {
                throw new RuntimeException(
                        "Payment amount mismatch"
                );
            }

            deductStock(order);

            order.setGatewayPaymentId(
                    request.razorpayPaymentId()
            );

            order.setPaymentStatus(PaymentStatus.PAID);
            order.setStatus(OrderStatus.CONFIRMED);

            orderRepository.save(order);

            cartItemsRepository.deleteAllByUserId(user.getId());

            return new PaymentVerificationResponse(
                    order.getOrderNumber(),
                    order.getStatus().name(),
                    order.getPaymentStatus().name(),
                    "Payment verified successfully"
            );

        } catch (RazorpayException e) {

            throw new RuntimeException(
                    "Unable to verify Razorpay payment",
                    e
            );
        }
    }

    private void deductStock(Order order) {

        for (var item : order.getItems()) {

            Long skuId = Long.parseLong(item.getSku());

            var sku = productSkuRepository.findByIdForUpdate(skuId)
                    .orElseThrow(() ->
                            new RuntimeException("SKU not found"));

            int quantity = item.getQuantity();

            if (sku.getStockQuantity() < quantity) {
                throw new RuntimeException(
                        "Insufficient stock for " + item.getProductName()
                );
            }

            sku.setStockQuantity(
                    sku.getStockQuantity() - quantity
            );
        }
    }
}