package com.omsoft.retail.user.service.impl;

import com.omsoft.retail.user.dto.AddressResponse;
import com.omsoft.retail.user.dto.UserRequest;
import com.omsoft.retail.user.dto.UserResponse;
import com.omsoft.retail.user.entiry.User;
import com.omsoft.retail.user.entiry.type.Role;
import com.omsoft.retail.user.repository.UserRepository;
import com.omsoft.retail.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse registerUser(UserRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = User.builder()
                .username(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword())) // later encrypt
                .role(Role.CUSTOMER)
                .build();

        User savedUser = userRepository.save(user);

        return mapToResponse(savedUser);
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return mapToResponse(user);
    }

    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .addresses(mapUserAddresses(user))
                .build();
    }
    private List<AddressResponse> mapUserAddresses(User user) {
       return Optional.ofNullable(user)
               .map(User::getAddresses)
               .orElse(Collections.emptyList())
               .stream()
               .map(addr -> new AddressResponse(
                       addr.getId(),
                       addr.getAddressLine1(),
                       addr.getCity(),
                       addr.getState(),
                       addr.getCountry(),
                       addr.isDefault()
               ))
               .toList();
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToResponse
                )
                .collect(Collectors.toList());
    }
}