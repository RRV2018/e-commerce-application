package com.omsoft.retail.user.service;

import com.omsoft.retail.user.dto.AddressRequest;
import com.omsoft.retail.user.entiry.Address;

import java.util.List;

public interface AddressService {
    Address addAddress(Long userId, AddressRequest dto);
    List<Address> getUserAddresses(Long userId);
    void deleteAddress(Long addressId);
}
