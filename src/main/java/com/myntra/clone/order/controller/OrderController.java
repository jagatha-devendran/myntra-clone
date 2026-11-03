package com.myntra.clone.order.controller;

import com.myntra.clone.order.Service.OrderService;
import com.myntra.clone.order.dto.OrderResponse;
import com.myntra.clone.order.dto.PlaceOrderRequest;
import com.myntra.clone.order.dto.PlaceOrderResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }


    @PostMapping
    public ResponseEntity<PlaceOrderResponse> placeOrder(
            @RequestBody PlaceOrderRequest request,
            Authentication authentication) {

        PlaceOrderResponse response =
                orderService.placeOrder(
                        authentication.getName(),
                        request
                );

        return ResponseEntity.ok(response);
    }


    @GetMapping
    public ResponseEntity<List<OrderResponse>> getMyOrders(
            Authentication authentication) {

        List<OrderResponse> response =
                orderService.getMyOrders(
                        authentication.getName()
                );

        return ResponseEntity.ok(response);
    }

    // 3. Get Individual Order Details
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderDetails(
            @PathVariable Long orderId,
            Authentication authentication) {

        OrderResponse response =
                orderService.getOrderDetails(
                        authentication.getName(),
                        orderId
                );

        return ResponseEntity.ok(response);
    }

    // 4. Cancel Order
    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<String> cancelOrder(
            @PathVariable Long orderId,
            Authentication authentication) {

        String response = orderService.cancelOrder(
                authentication.getName(),
                orderId
        );

        return ResponseEntity.ok(response);
    }
}