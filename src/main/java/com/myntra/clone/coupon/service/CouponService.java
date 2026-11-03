package com.myntra.clone.coupon.service;

import com.myntra.clone.cart.entity.CartItems;
import com.myntra.clone.cart.repository.CartRepository;
import com.myntra.clone.coupon.dto.ApplyCouponRequest;
import com.myntra.clone.coupon.dto.ApplyCouponResponse;
import com.myntra.clone.coupon.dto.CouponDto;
import com.myntra.clone.coupon.entity.Coupon;
import com.myntra.clone.coupon.repository.CouponRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
public class CouponService {

    private final CouponRepository couponRepository;
    private final CartRepository cartRepository;

    public CouponService(CouponRepository couponRepository, CartRepository cartRepository) {
        this.couponRepository = couponRepository;
        this.cartRepository = cartRepository;
    }

    public List<CouponDto> getAvailableCoupons() {
        List<Coupon> coupons = couponRepository.findByIsActiveTrue();
        return coupons.stream().map(this::mapToDto).toList();
    }

    public ApplyCouponResponse applyCoupon(ApplyCouponRequest request, Long userId) {
        if (request == null || request.code() == null || request.code().trim().isEmpty()) {
            return new ApplyCouponResponse(
                    false,
                    "Please enter a valid coupon code",
                    null, null, BigDecimal.ZERO, BigDecimal.ZERO,
                    BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO
            );
        }

        String code = request.code().trim().toUpperCase();
        Optional<Coupon> optionalCoupon = couponRepository.findByCodeIgnoreCase(code);

        if (optionalCoupon.isEmpty()) {
            return new ApplyCouponResponse(
                    false,
                    "Coupon code \"" + code + "\" is invalid",
                    code, null, BigDecimal.ZERO, BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    request.cartTotal() != null ? request.cartTotal() : BigDecimal.ZERO,
                    request.cartTotal() != null ? request.cartTotal() : BigDecimal.ZERO
            );
        }

        Coupon coupon = optionalCoupon.get();

        if (coupon.getIsActive() != null && !coupon.getIsActive()) {
            return new ApplyCouponResponse(
                    false,
                    "Coupon code \"" + code + "\" is expired or inactive",
                    code, null, BigDecimal.ZERO, BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    request.cartTotal() != null ? request.cartTotal() : BigDecimal.ZERO,
                    request.cartTotal() != null ? request.cartTotal() : BigDecimal.ZERO
            );
        }

        BigDecimal orderAmount = request.cartTotal();
        if ((orderAmount == null || orderAmount.compareTo(BigDecimal.ZERO) <= 0) && userId != null) {
            orderAmount = calculateUserCartTotal(userId);
        }
        if (orderAmount == null) {
            orderAmount = BigDecimal.ZERO;
        }

        if (coupon.getMinOrderValue() != null && orderAmount.compareTo(coupon.getMinOrderValue()) < 0) {
            return new ApplyCouponResponse(
                    false,
                    "Minimum order value of ₹" + coupon.getMinOrderValue().stripTrailingZeros().toPlainString() + " required to apply this coupon",
                    coupon.getCode(),
                    coupon.getDiscountType(),
                    coupon.getDiscountValue(),
                    coupon.getMinOrderValue(),
                    BigDecimal.ZERO,
                    orderAmount,
                    orderAmount
            );
        }

        BigDecimal discountAmount = calculateDiscount(coupon, orderAmount);
        BigDecimal finalAmount = orderAmount.subtract(discountAmount).max(BigDecimal.ZERO);

        return new ApplyCouponResponse(
                true,
                "Coupon \"" + coupon.getCode() + "\" applied successfully! You saved ₹" + discountAmount.setScale(2, RoundingMode.HALF_UP).toPlainString(),
                coupon.getCode(),
                coupon.getDiscountType(),
                coupon.getDiscountValue(),
                coupon.getMinOrderValue(),
                discountAmount.setScale(2, RoundingMode.HALF_UP),
                orderAmount.setScale(2, RoundingMode.HALF_UP),
                finalAmount.setScale(2, RoundingMode.HALF_UP)
        );
    }

    public BigDecimal calculateDiscount(Coupon coupon, BigDecimal orderAmount) {
        if (coupon == null || orderAmount == null || orderAmount.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal discount = BigDecimal.ZERO;
        if ("FLAT".equalsIgnoreCase(coupon.getDiscountType())) {
            discount = coupon.getDiscountValue();
        } else if ("PERCENTAGE".equalsIgnoreCase(coupon.getDiscountType())) {
            BigDecimal percentage = coupon.getDiscountValue().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
            discount = orderAmount.multiply(percentage).setScale(2, RoundingMode.HALF_UP);
        }

        return discount.min(orderAmount);
    }

    private BigDecimal calculateUserCartTotal(Long userId) {
        List<CartItems> items = cartRepository.findCartItemsWithProduct(userId);
        if (items == null || items.isEmpty()) {
            return BigDecimal.ZERO;
        }
        BigDecimal total = BigDecimal.ZERO;
        for (CartItems item : items) {
            if (item.getSku() != null && item.getSku().getProduct() != null && item.getSku().getProduct().getPrice() != null) {
                total = total.add(item.getSku().getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            }
        }
        return total;
    }

    private CouponDto mapToDto(Coupon coupon) {
        String title;
        String desc;
        if ("FLAT".equalsIgnoreCase(coupon.getDiscountType())) {
            title = "Flat ₹" + coupon.getDiscountValue().stripTrailingZeros().toPlainString() + " OFF";
            desc = "Save ₹" + coupon.getDiscountValue().stripTrailingZeros().toPlainString() + " on orders above ₹" + coupon.getMinOrderValue().stripTrailingZeros().toPlainString();
        } else {
            title = "Flat " + coupon.getDiscountValue().stripTrailingZeros().toPlainString() + "% OFF";
            desc = "Save " + coupon.getDiscountValue().stripTrailingZeros().toPlainString() + "% on orders above ₹" + coupon.getMinOrderValue().stripTrailingZeros().toPlainString();
        }

        return new CouponDto(
                coupon.getId(),
                coupon.getCode(),
                coupon.getDiscountType(),
                coupon.getDiscountValue(),
                coupon.getMinOrderValue(),
                coupon.getIsActive(),
                title,
                desc
        );
    }
}
