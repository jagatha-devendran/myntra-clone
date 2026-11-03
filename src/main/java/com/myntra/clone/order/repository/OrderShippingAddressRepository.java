package com.myntra.clone.order.repository;

import com.myntra.clone.order.entity.OrderShippingAddress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderShippingAddressRepository
        extends JpaRepository<OrderShippingAddress, Long> {
}