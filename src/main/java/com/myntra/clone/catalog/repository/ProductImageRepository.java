package com.myntra.clone.catalog.repository;

import com.myntra.clone.catalog.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    Optional<ProductImage> findFirstByProductIdOrderBySortOrderAsc(Long product_id);
}
