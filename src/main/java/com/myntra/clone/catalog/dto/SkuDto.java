package com.myntra.clone.catalog.dto;

public record SkuDto(
        Long skuId,
        String size,
        Integer stock,
        Boolean available
) {}
