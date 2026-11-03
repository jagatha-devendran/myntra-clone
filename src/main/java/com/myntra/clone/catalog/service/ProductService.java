package com.myntra.clone.catalog.service;

import com.myntra.clone.catalog.dto.ProductDetailResponse;
import com.myntra.clone.catalog.dto.ProductListingResponse;

public interface ProductService {
    public ProductListingResponse getProductsByCategory(String categorySlug, String filter, int page, int size, Long userId);
    public ProductDetailResponse getProductDetails(Long id, Long userId);
    }
