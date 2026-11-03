package com.myntra.clone.user.repository;

import com.myntra.clone.user.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthRepository extends JpaRepository<Users, Long> {

    Users findByEmail(String email);

//    Users findById(Long userId);
}
