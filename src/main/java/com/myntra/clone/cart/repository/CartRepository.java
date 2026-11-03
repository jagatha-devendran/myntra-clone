package com.myntra.clone.cart.repository;

import com.myntra.clone.cart.entity.CartItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CartRepository extends JpaRepository<CartItems, Long> {

        @Query("""
        SELECT c FROM CartItems c
        JOIN FETCH c.sku s
        JOIN FETCH s.product
        WHERE c.userId = :userId
    """)
        List<CartItems> findCartItemsWithProduct(
                @Param("userId") Long userId
        );

        @Query("""
        SELECT c
        FROM CartItems c
        JOIN FETCH c.sku s
        JOIN FETCH s.product p
        WHERE c.userId = :userId
        ORDER BY c.createdAt DESC
    """)
        List<CartItems> findCartItemsWithProducts(
                @Param("userId") Long userId
        );

        void deleteAllByUserId(Long userId);



}
