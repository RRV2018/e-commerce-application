package com.omsoft.retail.user.service.impl;

import com.omsoft.retail.user.dto.AddressRequest;
import com.omsoft.retail.user.entiry.Address;
import com.omsoft.retail.user.entiry.User;
import com.omsoft.retail.user.repository.AddressRepository;
import com.omsoft.retail.user.repository.UserRepository;
import com.omsoft.retail.user.service.AddressService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    public AddressServiceImpl(AddressRepository addressRepository,
                          UserRepository userRepository) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
    }
    @CircuitBreaker(name = "userServiceCB", fallbackMethod = "fallback")
    @Override
    public Address addAddress(Long userId, AddressRequest dto) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // If default = true, unset previous default
        if (dto.isDefault()) {
            addressRepository.findByUserIdAndIsDefaultTrue(userId)
                    .ifPresent(addr -> {
                        addr.setDefault(false);
                        addressRepository.save(addr);
                    });
        }

        Address address = new Address();
        address.setUser(user);
        address.setAddressLine1(dto.getAddressLine1());
        address.setAddressLine2(dto.getAddressLine2());
        address.setCity(dto.getCity());
        address.setState(dto.getState());
        address.setCountry(dto.getCountry());
        address.setPostalCode(dto.getPostalCode());
        address.setAddressType(dto.getAddressType());
        address.setDefault(dto.isDefault());

        return addressRepository.save(address);
    }
    @Override
    public List<Address> getUserAddresses(Long userId) {
        return addressRepository.findByUserId(userId);
    }

    @Override
    public void deleteAddress(Long addressId) {
        addressRepository.deleteById(addressId);
    }

    // Fallback must match method signature + Throwable
    public String fallback(String userId, Throwable ex) {
        log.error("Fallback triggered for userId={}, reason={}", userId, ex.getMessage());
        return "User service is temporarily unavailable";
    }
}
