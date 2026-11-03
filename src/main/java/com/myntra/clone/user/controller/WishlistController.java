package com.myntra.clone.user.controller;

import com.myntra.clone.user.dto.AddToWishlistResponse;
import com.myntra.clone.user.dto.RemoveWishListResponse;
import com.myntra.clone.user.dto.WishListResponse;
import com.myntra.clone.user.entity.Users;
import com.myntra.clone.user.service.AuthenticatedUserService;
import com.myntra.clone.user.service.WishlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/wishlist")
public class WishlistController {


    private final WishlistService wishlistService;
    private final AuthenticatedUserService authenticatedUserService;

    public WishlistController(
            WishlistService wishlistService,
            AuthenticatedUserService authenticatedUserService) {

        this.wishlistService = wishlistService;
        this.authenticatedUserService = authenticatedUserService;
    }

    @PostMapping("/{productId}")
    public ResponseEntity<AddToWishlistResponse> addToWishList(@PathVariable("productId") Long productId){

        Long userId = authenticatedUserService.getCurrentUserId();


        AddToWishlistResponse response = wishlistService.saveWishlist(userId, productId);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<RemoveWishListResponse> deleteFromWishList(@PathVariable("productId") Long productId){

        Long userId = authenticatedUserService.getCurrentUserId();

        RemoveWishListResponse response = wishlistService.removeProduct(userId, productId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/")
    public ResponseEntity<WishListResponse> getWishList(){

        Long userId = authenticatedUserService.getCurrentUserId();

        WishListResponse response = wishlistService.getWishList(userId);

        return ResponseEntity.ok(response);
    }


}
