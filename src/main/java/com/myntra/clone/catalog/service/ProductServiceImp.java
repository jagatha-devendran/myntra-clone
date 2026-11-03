package com.myntra.clone.catalog.service;

import com.myntra.clone.catalog.dto.ProductDetailResponse;
import com.myntra.clone.catalog.dto.ProductListingResponse;
import com.myntra.clone.catalog.dto.ProductSummaryDto;
import com.myntra.clone.catalog.dto.SkuDto;
import com.myntra.clone.catalog.entity.Product;
import com.myntra.clone.catalog.entity.ProductImage;
import com.myntra.clone.catalog.repository.ProductImageRepository;
import com.myntra.clone.catalog.repository.ProductRepository;
import com.myntra.clone.user.repository.WishlistRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImp implements ProductService {
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final WishlistRepository wishlistRepository;

    public ProductServiceImp(ProductRepository productRepository,
                             ProductImageRepository productImageRepository, WishlistRepository wishlistRepository) {
        this.productRepository = productRepository;
        this.productImageRepository = productImageRepository;
        this.wishlistRepository = wishlistRepository;
    }

    @Override
    public ProductListingResponse getProductsByCategory(String categorySlug, String filter, int page, int size, Long userId) {

        int zeroBasedPage = Math.max(0, page-1);
        Pageable pageable = PageRequest.of(zeroBasedPage, size);

        String normalizedCategory = categorySlug.replace("-", " ").trim();

        String brandFilter = null;
        if (filter != null && filter.toLowerCase().startsWith("brand:")){
            brandFilter = filter.substring(6).trim();
        }

        Page<Product> productPage;
        if (brandFilter != null && !brandFilter.isEmpty()){
            productPage = productRepository.findByCategoryAndBrand(normalizedCategory, brandFilter, pageable);
        } else{
            productPage = productRepository.findByCategory(normalizedCategory, pageable);
        }

        List<ProductSummaryDto> dtoList = productPage.getContent().stream()
                                        .map(product -> mapToSummaryDto(product, userId)).toList();

        return new ProductListingResponse(
                productPage.getTotalElements(),
                page,
                size,
                dtoList
        );
    }

    private ProductSummaryDto mapToSummaryDto(Product product, Long userId) {
        List<String> imageUrls = product.getImages().stream()
                .map(ProductImage::getImageUrl)
                .toList();

        boolean isWishlisted =
                wishlistRepository.existsByUserIdAndProductId(
                        userId, product.getId()
                );

        return new ProductSummaryDto(
                product.getMyntraProductId() != null? product.getMyntraProductId() : product.getId(),
                product.getTitle(),
                product.getBrand().getName(),
                product.getCategory(),
                product.getGender(),
                product.getPrimaryColour(),
                product.getMrp(),
                product.getPrice(),
                product.getDiscountLabel(),
                product.getRating(),
                product.getRatingCount(),
                imageUrls,
                isWishlisted
        );
    }

    public ProductDetailResponse getProductDetails(Long id, Long userId) {
        Product product = productRepository.findActiveProductWithBrandById(id);

        List<String> imageUrls = product.getImages().stream()
                .map(ProductImage::getImageUrl)
                .toList();

        List<SkuDto> skuDtos = product.getSkus().stream()
                .map(sku -> new SkuDto(
                        sku.getId(),
                        sku.getSize(),
                        sku.getStockQuantity(),
                        sku.getIsAvailable() != null && sku.getIsAvailable() && sku.getStockQuantity() > 0
                ))
                .toList();

        Long returnId = product.getMyntraProductId() != null ? product.getMyntraProductId() : product.getId();

        boolean isWishlisted =
                wishlistRepository.existsByUserIdAndProductId(
                        userId, product.getId()
                );

        return new ProductDetailResponse(
                returnId,
                product.getTitle(),
                product.getBrand().getName(),
                product.getCategory(),
                product.getGender(),
                product.getPrimaryColour(),
                product.getMrp(),
                product.getPrice(),
                product.getDiscountLabel(),
                product.getRating(),
                product.getRatingCount(),
                imageUrls,
                skuDtos,
                isWishlisted
        );


    }
}
