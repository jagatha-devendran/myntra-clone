package com.myntra.clone.user.service;

import com.myntra.clone.catalog.entity.Product;
import com.myntra.clone.catalog.entity.ProductImage;
import com.myntra.clone.catalog.repository.ProductRepository;
import com.myntra.clone.user.dto.AddToWishlistResponse;
import com.myntra.clone.user.dto.RemoveWishListResponse;
import com.myntra.clone.user.dto.WishListItems;
import com.myntra.clone.user.dto.WishListResponse;
import com.myntra.clone.user.entity.Users;
import com.myntra.clone.user.entity.Wishlist;
import com.myntra.clone.user.repository.AuthRepository;
import com.myntra.clone.user.repository.WishlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final AuthRepository authRepository;
    private final ProductRepository productRepository;

    @Autowired
    public WishlistService(WishlistRepository wishlistRepository, AuthRepository authRepository,
                           ProductRepository productRepository){
        this.wishlistRepository = wishlistRepository;
        this.authRepository = authRepository;
        this.productRepository = productRepository;
    }

    public AddToWishlistResponse saveWishlist(Long userId, Long productId){

        Users user = authRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found: " + userId));

        Product product = productRepository.findByMyntraProductIdOrId(productId, productId)
                .orElseThrow(() ->
                        new RuntimeException("Product not found: " + productId));

        Wishlist wishlist = new Wishlist(user, product);

        wishlistRepository.save(wishlist);

        return new AddToWishlistResponse("Added to wishlist", wishlist.getId(), productId);
    }

    @Transactional
    public RemoveWishListResponse removeProduct(Long userId, Long myntraProductId) {

        Product product = productRepository
                .findByMyntraProductIdOrId(myntraProductId, myntraProductId)
                .orElseThrow(() ->
                        new RuntimeException("Product not found"));

        wishlistRepository.deleteByUserIdAndProductId(
                userId,
                product.getId()
        );

        return new RemoveWishListResponse(
                "Product removed from Wishlist Successfully",
                myntraProductId
        );
    }
    public WishListResponse getWishList(Long userId){

        Users user = authRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found: " + userId));;

        List<Wishlist> wishlists = wishlistRepository.findByUserId(userId);

        List<WishListItems> wishListItems = new ArrayList<>();

        for(Wishlist wishlist : wishlists){

            Product product = wishlist.getProduct();

            List<String> images = product.getImages()
                    .stream()
                    .map(ProductImage::getImageUrl)
                    .toList();

            WishListItems item = new WishListItems(
                    wishlist.getId(),
                    product.getId(),
                    product.getTitle(),
                    product.getBrand().getName(),
                    product.getPrice(),
                    product.getMrp(),
                    product.getDiscountLabel(),
                    product.getRating(),
                    product.getRatingCount(),
                    images
            );

            wishListItems.add(item);
        }

        return new WishListResponse(wishListItems.size(), wishListItems);

    }
}
