package com.myntra.clone.order.dto;

public record PlaceOrderRequest(
        Long addressId,
        Long couponId,
        PaymentMethod paymentMethod){

}
