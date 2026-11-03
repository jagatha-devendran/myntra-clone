package com.myntra.clone.coupon.controller;

import com.myntra.clone.coupon.dto.ApplyCouponRequest;
import com.myntra.clone.coupon.dto.ApplyCouponResponse;
import com.myntra.clone.coupon.dto.CouponDto;
import com.myntra.clone.coupon.service.CouponService;
import com.myntra.clone.user.entity.Users;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping({"/gateway/v1/coupons", "/coupons"})
@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
public class CouponController {

    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @GetMapping
    public ResponseEntity<List<CouponDto>> getAvailableCoupons() {
        return ResponseEntity.ok(couponService.getAvailableCoupons());
    }

    @PostMapping("/apply")
    public ResponseEntity<ApplyCouponResponse> applyCoupon(@RequestBody ApplyCouponRequest request) {
        Long userId = getAuthenticatedUserId();
        return ResponseEntity.ok(couponService.applyCoupon(request, userId));
    }

    @PostMapping("/remove")
    public ResponseEntity<Map<String, Object>> removeCoupon() {
        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Coupon removed successfully"
        ));
    }

    private Long getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Users user) {
            return user.getId();
        }
        return null;
    }
}
