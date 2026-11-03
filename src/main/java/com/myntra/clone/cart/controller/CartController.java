package com.myntra.clone.cart.controller;

import com.myntra.clone.cart.Service.CartService;
import com.myntra.clone.cart.dto.*;
import com.myntra.clone.user.entity.Users;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Controller
@RequestMapping("/gateway/v1/cart/default")
public class CartController {

    private CartService cartService;

    public CartController(CartService theCartService){
        cartService = theCartService;
    }

    @PostMapping("/add")
    public ResponseEntity<AddToCartResponseDto> addProductToCart(@RequestBody AddToCartRequestDto addToCartRequestDto){
        Long userId = getAuthenticatedUserId();

        System.out.println(userId);

        if (userId == null) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        AddToCartResponseDto response = cartService.addToCart(userId, addToCartRequestDto.skuId(), addToCartRequestDto.quantity());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/checkout/cart")
    public ResponseEntity<CheckoutCartResponse> getCart(
            Authentication authentication) {

        Long userId = getAuthenticatedUserId();

        if (userId == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "User not found"
            );
        }

        return ResponseEntity.ok(
                cartService.getCheckoutCart(userId)
        );
    }
    @PutMapping("/edit")
    public ResponseEntity<CartActionResponse> editCart(
            @RequestBody EditCartRequest request) {

        Long userId = getAuthenticatedUserId();

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(
                cartService.editCart(userId, request)
        );
    }

    @PutMapping("/remove")
    public ResponseEntity<CartActionResponse> removeCart(
            @RequestBody RemoveCartRequest request) {

        Long userId = getAuthenticatedUserId();

        return ResponseEntity.ok(
                cartService.removeCart(userId, request)
        );
    }

    public Long getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof Users user) {
            return user.getId();
        }

        return null;
    }


}
