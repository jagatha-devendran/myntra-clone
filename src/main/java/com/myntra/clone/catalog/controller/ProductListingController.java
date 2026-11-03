package com.myntra.clone.catalog.controller;

import com.myntra.clone.catalog.dto.ProductListingResponse;
import com.myntra.clone.catalog.service.ProductService;
import com.myntra.clone.user.entity.Users;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/gateway/v4/search")
public class ProductListingController {

    private final ProductService productService;

    ProductListingController(ProductService theProductService, ProductService productService){
        this.productService = productService;

    }

    @GetMapping("/{categorySlug}")
    public ResponseEntity<ProductListingResponse> getProducts(
            @PathVariable("categorySlug") String categorySlug,
            @RequestParam(value = "f", required = false) String filter,
            @RequestParam(value = "p", defaultValue = "1") int page,
            @RequestParam(value = "rows", defaultValue = "50") int rows
    ){
        Long userId = getAuthenticatedUserId();
        ProductListingResponse response = productService.getProductsByCategory(categorySlug, filter, page, rows, userId);
        return ResponseEntity.ok(response);
    }

    public Long getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof Users user) {
            return user.getId();
        }

        return null;
    }


}
