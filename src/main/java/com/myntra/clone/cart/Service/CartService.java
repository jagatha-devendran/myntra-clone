package com.myntra.clone.cart.Service;
import com.myntra.clone.cart.dto.*;
import com.myntra.clone.cart.entity.CartItems;
import com.myntra.clone.cart.repository.CartRepository;
import com.myntra.clone.catalog.entity.Product;
import com.myntra.clone.catalog.entity.ProductImage;
import com.myntra.clone.catalog.entity.ProductSku;
import com.myntra.clone.catalog.repository.ProductImageRepository;
import com.myntra.clone.catalog.repository.ProductSkuRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CartService {

    private CartRepository cartRepository;
    private ProductSkuRepository productSkuRepository;
    private final ProductImageRepository productImageRepository;


    public CartService(CartRepository theCartRepository, ProductSkuRepository theProductSkuRepository, ProductImageRepository theProductImageRepository){
        cartRepository = theCartRepository;
        productSkuRepository = theProductSkuRepository;
        productImageRepository = theProductImageRepository;
    }

    public CheckoutCartResponse getCheckoutCart(Long userId) {

        List<CartItems> cartItems =
                cartRepository.findCartItemsWithProducts(userId);

        List<CartProductResponse> products = new ArrayList<>();

        BigDecimal cartTotal = BigDecimal.ZERO;
        int cartItemCount = 0;

        for (CartItems item : cartItems) {

            ProductSku sku = item.getSku();
            Product product = sku.getProduct();

            // Get only one image
            String imageUrl = productImageRepository
                    .findFirstByProductIdOrderBySortOrderAsc(
                            product.getId()
                    )
                    .map(ProductImage::getImageUrl)
                    .orElse(null);

            // Price * Quantity
            BigDecimal subtotal = product.getPrice()
                    .multiply(BigDecimal.valueOf(item.getQuantity()));

            cartTotal = cartTotal.add(subtotal);
            cartItemCount += item.getQuantity();

            CartProductResponse response = new CartProductResponse(
                    item.getId(),
                    product.getMyntraProductId(),
                    product.getTitle(),
                    product.getBrand().getName(),
                    sku.getId(),
                    sku.getSize(),
                    item.getQuantity(),
                    product.getMrp(),
                    product.getPrice(),
                    subtotal,
                    imageUrl
            );

            products.add(response);
        }

        return new CheckoutCartResponse(
                cartItemCount,
                cartTotal,
                products
        );
    }

    public AddToCartResponseDto addToCart(Long userId, Long skuId, int quantity){

        ProductSku sku = productSkuRepository.findById(skuId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Product SKU not found"
                ));

        CartItems cartItems = new CartItems(userId, sku, quantity, LocalDateTime.now());
        cartRepository.save(cartItems);
        return new AddToCartResponseDto("PRODUCT_GOT_ADDED_SUCCESSFULLY", "ADD_TO_CART");
    }

    public CartActionResponse removeCart(
            Long userId,
            RemoveCartRequest request) {

        CartItems item = cartRepository
                .findById(request.itemId())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Cart item not found"
                        )
                );

        if (!item.getUserId().equals(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Cart item not found"
            );
        }

        cartRepository.delete(item);
        cartRepository.flush();

        return buildResponse(
                userId,
                "Product removed from cart successfully"
        );
    }

    public CartActionResponse editCart(
            Long userId, EditCartRequest request) {

        CartItems item = cartRepository
                .findById(request.itemId())
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Cart item not found"
                        )
                );


        if (request.quantity() != null) {
            if (request.quantity() < 1) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Quantity must be at least 1"
                );
            }

            item.setQuantity(request.quantity());
        }
        ProductSku sku = productSkuRepository.findById(request.skuId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Product SKU not found"
                ));
        // Update selected SKU size
        if (sku != null) {
            item.setSku(sku);
        }

        cartRepository.save(item);


        return buildResponse(
                item.getUserId(),
                "Product updated successfully"
        );
    }

    private CartActionResponse buildResponse(
            Long userId,
            String message) {

        List<CartItems> items =
                cartRepository.findCartItemsWithProduct(userId);

        int itemCount = items.stream()
                .mapToInt(CartItems::getQuantity)
                .sum();

        BigDecimal cartTotal = calculateCartTotal(items);

        return new CartActionResponse(
                true,
                message,
                itemCount,
                cartTotal
        );
    }

    private BigDecimal calculateCartTotal(List<CartItems> items) {

        return items.stream()
                .map(item -> {
                    BigDecimal price = item.getSku()
                            .getProduct()
                            .getPrice();

                    return price.multiply(
                            BigDecimal.valueOf(item.getQuantity())
                    );
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
