package com.myntra.clone.user.service;

import com.myntra.clone.user.entity.Users;

public interface AuthService {

    String login(String email, String password);

    void registerNewUser(Users user);
}
