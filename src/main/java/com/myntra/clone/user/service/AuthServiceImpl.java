package com.myntra.clone.user.service;

import com.myntra.clone.user.entity.Users;
import com.myntra.clone.user.repository.AuthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private AuthRepository authRepository;
    private PasswordEncoder passwordEncoder;
    private JwtService jwtService;

    @Autowired
    public AuthServiceImpl(AuthRepository theAuthRepository,
                           PasswordEncoder thePasswordEncoder,
                           JwtService theJwtService) {
        authRepository = theAuthRepository;
        passwordEncoder = thePasswordEncoder;
        jwtService = theJwtService;
    }

    @Override
    public String login(String email, String password) {
        Users user = authRepository.findByEmail(email);

        // Same check as the expense tracker, but guard against an unknown email
        // (there user.getPassword() would throw a NullPointerException).
        boolean passwordCorrect = user != null
                && passwordEncoder.matches(password, user.getPassword());
        if (!passwordCorrect) {
            throw new RuntimeException("Invalid email or password");
        }
        return jwtService.generateToken(user.getId(), user.getEmail());
    }

    @Override
    public void registerNewUser(Users user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        authRepository.save(user);
    }
}
