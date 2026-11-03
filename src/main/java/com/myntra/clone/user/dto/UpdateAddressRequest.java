package com.myntra.clone.user.dto;

import java.util.List;

public record UpdateAddressRequest(
        String id,
        String unifiedId,
        Boolean isDefault,
        String name,
        String mobile,
        String email,
        String houseNumber,
        String streetAddress,
        String locality,
        String landmark,
        String city,
        String state,
        String pincode,
        String addressType,
        List<String> notAvailableDays
) {
}
