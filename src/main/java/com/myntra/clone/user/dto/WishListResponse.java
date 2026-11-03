package com.myntra.clone.user.dto;

import java.util.List;

public record WishListResponse(
        int totalCount,
        List<WishListItems> wishListItems
) {
}
