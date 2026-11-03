package com.myntra.clone.catalog.dto;

import java.math.BigDecimal;
import java.util.List;

public record ProductSummaryDto(Long productId,
                                String title,
                                String brand,
                                String category,
                                String gender,
                                String primaryColour,
                                BigDecimal mrp,
                                BigDecimal price,
                                String discountDisplayLabel,
                                BigDecimal rating,
                                Integer ratingCount,
                                List<String> images,
                                boolean isWishlisted) {}
