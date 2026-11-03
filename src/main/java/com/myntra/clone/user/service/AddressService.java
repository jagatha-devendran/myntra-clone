package com.myntra.clone.user.service;

import com.myntra.clone.user.dto.AddressListResponse;
import com.myntra.clone.user.dto.AddressRequest;
import com.myntra.clone.user.dto.AddressResponse;
import com.myntra.clone.user.dto.DeleteAddressResponse;
import com.myntra.clone.user.entity.Address;
import com.myntra.clone.user.entity.Users;
import com.myntra.clone.user.repository.AddressRepository;
import com.myntra.clone.user.repository.AuthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AddressService {

    private AddressRepository addressRepository;
    private AuthRepository authRepository;

    @Autowired
    public AddressService(AddressRepository theAddressRepository, AuthRepository theAuthRepository){
        addressRepository = theAddressRepository;
        authRepository = theAuthRepository;
    }

    public AddressResponse saveAddress(Long userId, AddressRequest addressRequest){

        Optional<Users> optionalUser = authRepository.findById(userId);

        Address address = new Address(optionalUser.get(), addressRequest.name(), addressRequest.mobile(), addressRequest.streetAddress(),
                addressRequest.city(), addressRequest.state(), addressRequest.pincode(), addressRequest.isDefault());

        addressRepository.save(address);

        return  mapToResponse(address);

    }

    public AddressResponse editAddress(Long addressId, Long userId, AddressRequest addressRequest){

        Address address = addressRepository.findByIdAndUserId(addressId, userId).orElseThrow(() -> new RuntimeException("Address not found"));

        address.setRecipientName(addressRequest.name());
        address.setPhone(addressRequest.mobile());
        address.setStreetAddress(addressRequest.streetAddress());
        address.setCity(addressRequest.city());
        address.setState(addressRequest.state());
        address.setPincode(addressRequest.pincode());
        address.setDefault(addressRequest.isDefault());

        Address updatedAddress = addressRepository.save(address);

        return mapToResponse(updatedAddress);

    }

    public DeleteAddressResponse deleteAddress(Long addressId,Long userId){

        Address address = addressRepository
                .findByIdAndUserId(addressId, userId)
                .orElseThrow(() ->
                        new RuntimeException("Address not found"));

        addressRepository.delete(address);

        return new DeleteAddressResponse(
                "ADDRESS_DELETED",
                "SUCCESS",
                "15004"
        );

    }

    public AddressListResponse getAll(Long userId){

        List<Address> addressList = addressRepository.findAllByUserId(userId);

        List<AddressResponse> addresses = new ArrayList<>();

        for (Address address : addressList){
            addresses.add(mapToResponse(address));
        }

//        List<AddressResponse> addresses =
//                addressRepository.findAllByUserId(userId)
//                        .stream()
//                        .map(this::mapToResponse)
//                        .toList();

        return new AddressListResponse(addresses);

    }

    private AddressResponse mapToResponse(Address address) {

        return new AddressResponse(
                address.getId(),
                address.getDefault(),
                true,
                address.getRecipientName(),
                address.getPhone(),
                null,
                null,
                address.getStreetAddress(),
                null,
                null,
                address.getCity(),
                address.getState(),
                address.getPincode()
        );
    }


}
