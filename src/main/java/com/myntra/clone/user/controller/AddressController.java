package com.myntra.clone.user.controller;

import com.myntra.clone.user.dto.AddressListResponse;
import com.myntra.clone.user.dto.AddressRequest;
import com.myntra.clone.user.dto.AddressResponse;
import com.myntra.clone.user.dto.DeleteAddressResponse;
import com.myntra.clone.user.entity.Users;
import com.myntra.clone.user.service.AddressService;
import com.myntra.clone.user.service.AuthenticatedUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/v2/addresses")
public class AddressController {


    private final AddressService addressService;
    private final AuthenticatedUserService authenticatedUserService;

    public AddressController(
            AddressService addressService,
            AuthenticatedUserService authenticatedUserService) {

        this.addressService = addressService;
        this.authenticatedUserService = authenticatedUserService;
    }

    @PostMapping("/")
    public ResponseEntity<AddressResponse> addAddress(@RequestBody AddressRequest addressRequest){

        Long userId = authenticatedUserService.getCurrentUserId();

        AddressResponse response = addressService.saveAddress(userId, addressRequest);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/unifiedIds/{addressId}")
    public ResponseEntity<AddressResponse> editAddress(@PathVariable Long addressId, @RequestBody AddressRequest addressRequest){

        Long userId = authenticatedUserService.getCurrentUserId();

        AddressResponse response = addressService.editAddress(addressId, userId, addressRequest);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/unifiedIds/{addressId}")
    public ResponseEntity<DeleteAddressResponse> deleteAddress(@PathVariable Long addressId){

        Long userId = authenticatedUserService.getCurrentUserId();

        DeleteAddressResponse response = addressService.deleteAddress(addressId, userId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/my/address")
    public ResponseEntity<AddressListResponse> getAllAddresses(){

        Long userId = authenticatedUserService.getCurrentUserId();

        AddressListResponse response = addressService.getAll(userId);

        return ResponseEntity.ok(response);
    }

}
