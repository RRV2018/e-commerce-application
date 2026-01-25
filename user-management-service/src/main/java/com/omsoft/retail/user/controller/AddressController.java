package com.omsoft.retail.user.controller;

import com.omsoft.retail.user.dto.AddressRequest;
import com.omsoft.retail.user.entiry.Address;
import com.omsoft.retail.user.service.AddressService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user/{userId}/addresses")
@Tag(name = "Address", description = "User Address APIs")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @PostMapping
    public ResponseEntity<Address> addAddress(@PathVariable Long userId,
            @Valid @RequestBody AddressRequest dto) {

        return ResponseEntity.ok(addressService.addAddress(userId, dto));
    }

    @GetMapping
    public ResponseEntity<List<Address>> getAddresses(@PathVariable Long userId) {
        return ResponseEntity.ok(addressService.getUserAddresses(userId));
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> deleteAddress(
            @PathVariable Long addressId) {
        addressService.deleteAddress(addressId);
        return ResponseEntity.noContent().build();
    }
}
