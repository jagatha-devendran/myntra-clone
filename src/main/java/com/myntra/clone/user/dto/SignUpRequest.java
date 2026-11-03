package com.myntra.clone.user.dto;

public record SignUpRequest(
        String name,

        String email,

        String phone,

        String password
) {
}
