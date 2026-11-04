package com.myntra.clone.catalog.controller;

import com.myntra.clone.catalog.dto.ProductDetailResponse;
import com.myntra.clone.catalog.service.ProductService;
import com.myntra.clone.user.entity.Users;
import com.myntra.clone.user.service.AuthenticatedUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/gateway/v2/product")
public class ProductDetailController {

    private final ProductService productService;
    AuthenticatedUserService authenticatedUserService

    ProductDetailController(ProductService theProductService, AuthenticatedUserService theAuthenticatedUserService){

        productService = theProductService;
        authenticatedUserService = theAuthenticatedUserService;
    }

    @GetMapping("/{id}/details")
    public ResponseEntity<ProductDetailResponse> getProductDetails(@PathVariable("id") Long id){
        Long userId = authenticatedUserService.getCurrentUserId();
        ProductDetailResponse response = productService.getProductDetails(id, userId);
        return ResponseEntity.ok(response);
    }

}


