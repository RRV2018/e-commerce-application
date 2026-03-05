package com.omsoft.retail.user.service;

import com.omsoft.retail.user.dto.UserRequest;
import com.omsoft.retail.user.dto.UserResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    UserResponse registerUser(UserRequest request);
    UserResponse getUserById(Long id);
    UserResponse updateUser(Long id, UserRequest request);
    List<UserResponse> getAllUsers();
    Page<UserResponse> getUsersPage(Pageable pageable);
    boolean deleteUserById(Long id);
    UserResponse getUserByEmail(String email);
}
