package com.myntra.clone.user.controller;

import com.myntra.clone.user.dto.LoginRequest;
import com.myntra.clone.user.dto.SignUpRequest;
import com.myntra.clone.user.entity.Users;
import com.myntra.clone.user.repository.AuthRepository;
import com.myntra.clone.user.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = {"http://localhost:5173", "http://127.0.0.1:5173"})
@RestController
public class AuthController {

    private final AuthService authService;
    private final AuthRepository authRepository;

    @Autowired
    public AuthController(AuthService theAuthService, AuthRepository theAuthRepository) {
        this.authService = theAuthService;
        this.authRepository = theAuthRepository;
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        String token = authService.login(request.getEmail(), request.getPassword());

        // Create HttpOnly Cookie
        ResponseCookie cookie = ResponseCookie.from("jwt_token", token)
                .httpOnly(true)                      // Blocks JS from reading it (XSS protection)
                .secure(false)                       // Set to true in production (HTTPS)
                .path("/")                           // Available for all endpoints
                .maxAge(24 * 60 * 60)                // 1 day expiration
                .sameSite("Lax")                     // Protects against CSRF
                .build();

        Users currentUser = authRepository.findByEmail(request.getEmail());
        Map<String, Object> response = new HashMap<>();
        response.put("username", currentUser.getName());
        response.put("email", currentUser.getEmail());
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(response);
    }

    @PostMapping("/auth/signup")
    public void register(@RequestBody SignUpRequest signUpRequest) {
        Users user = new Users(signUpRequest.name(), signUpRequest.email(), signUpRequest.phone(), signUpRequest.password(), LocalDateTime.now());
        authService.registerNewUser(user);
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<?> logout() {
        ResponseCookie cookie = ResponseCookie.from("jwt_token", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0) // Immediately expires the cookie
                .sameSite("Lax")
                .build();

        System.out.println("Logged out successfully");

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body("Logged out successfully");
    }
}
