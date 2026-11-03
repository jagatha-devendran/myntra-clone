package com.myntra.clone.catalog.dto;

import java.util.List;

public record ProductListingResponse(
        long total,
        int page,
        int pageSize,
        List<ProductSummaryDto> products
) {
}
