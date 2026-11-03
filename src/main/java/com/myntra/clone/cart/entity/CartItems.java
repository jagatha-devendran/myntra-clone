package com.myntra.clone.cart.entity;

import com.myntra.clone.catalog.entity.ProductSku;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "cart_items")
public class CartItems {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sku_id")
    private ProductSku sku;

    @Column(name = "quantity")
    private int quantity;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public ProductSku getSku() {
        return sku;
    }

    public void setSku(ProductSku sku) {
        this.sku = sku;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public CartItems() {}

    public CartItems(Long id, Long userId, ProductSku sku, int quantity, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.sku = sku;
        this.quantity = quantity;
        this.createdAt = createdAt;
    }

    public CartItems(Long userId, ProductSku sku, int quantity, LocalDateTime createdAt) {
        this.userId = userId;
        this.sku = sku;
        this.quantity = quantity;
        this.createdAt = createdAt;
    }
}
