package com.myntra.clone.user.repository;

import com.myntra.clone.user.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    void deleteByUserIdAndProductId(Long userId, Long productId);

    List<Wishlist> findByUserId(Long userId);

    boolean existsByUserIdAndProductId(Long userId, Long productId);


}
