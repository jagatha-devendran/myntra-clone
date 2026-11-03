package com.myntra.clone.user.repository;

import com.myntra.clone.user.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<Address, Long> {

    Optional<Address> findByIdAndUserId(Long addressId, Long userId);

    List<Address> findAllByUserId(Long userId);
}
