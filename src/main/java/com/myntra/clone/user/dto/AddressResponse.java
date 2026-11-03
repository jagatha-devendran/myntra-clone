package com.myntra.clone.user.dto;

import java.util.List;

public record AddressResponse(
        Long id,
        Boolean isDefault,
        Boolean checkoutAllowed,
        String name,
        String mobile,
        String email,
        String houseNumber,
        String streetAddress,
        String locality,
        String landmark,
        String city,
        String state,
        String pincode
) {
}
