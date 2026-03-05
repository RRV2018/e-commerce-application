package com.omsoft.retail.user.service.impl;

import com.omsoft.retail.user.dto.AddressResponse;
import com.omsoft.retail.user.dto.UserRequest;
import com.omsoft.retail.user.dto.UserResponse;
import com.omsoft.retail.user.entiry.User;
import com.omsoft.retail.user.entiry.type.Role;
import com.omsoft.retail.user.exception.AlreadyExistsException;
import com.omsoft.retail.user.exception.UserNotFoundException;
import com.omsoft.retail.user.repository.UserRepository;
import com.omsoft.retail.user.service.UserService;
import com.omsoft.retail.user.util.EncryptDecryptUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EncryptDecryptUtil encryptDecryptUtil;

    @Override
    public UserResponse registerUser(UserRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new AlreadyExistsException("Email :", request.getEmail());
        }
        User user = User.builder()
                .username(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword())) // later encrypt
                .decryptablePassword(encryptDecryptUtil.encrypt(request.getPassword()))
                .role(Role.CUSTOMER)
                .build();

        User savedUser = userRepository.save(user);

        return mapToResponse(savedUser);
    }

    @Override
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        return mapToResponse(user);
    }

    @Override
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found for email: " + email));
        return mapToResponse(user);
    }

    @Override
    public UserResponse updateUser(Long id, UserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        userRepository.findByEmail(request.getEmail()).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new AlreadyExistsException("Email", request.getEmail());
            }
        });
        user.setUsername(request.getName());
        user.setEmail(request.getEmail());
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setDecryptablePassword(encryptDecryptUtil.encrypt(request.getPassword()));
        }
        User saved = userRepository.save(user);
        return mapToResponse(saved);
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
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public Page<UserResponse> getUsersPage(Pageable pageable) {
        return userRepository.findAll(pageable).map(this::mapToResponse);
    }

    @Override
    public boolean deleteUserById(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }
}