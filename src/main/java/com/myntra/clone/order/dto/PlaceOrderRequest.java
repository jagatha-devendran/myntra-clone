package com.myntra.clone.order.dto;

public record PlaceOrderRequest(
        Long addressId,
        PaymentMethod paymentMethod){

}
