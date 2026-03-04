package com.omsoft.retail.user.controller;

import com.omsoft.retail.user.dto.UserRequest;
import com.omsoft.retail.user.dto.UserResponse;
import com.omsoft.retail.user.entiry.type.Role;
import com.omsoft.retail.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    private UserController userController;

    @BeforeEach
    void setUp() {
        userController = new UserController(userService);
    }

    @Test
    void registerUser_returnsCreatedUser() {
        UserRequest request = new UserRequest();
        request.setName("John");
        request.setEmail("john@example.com");
        request.setPassword("password123");
        UserResponse responseBody = UserResponse.builder()
                .id(1L)
                .name("John")
                .email("john@example.com")
                .role(Role.CUSTOMER)
                .addresses(Collections.emptyList())
                .build();
        when(userService.registerUser(any(UserRequest.class))).thenReturn(responseBody);

        ResponseEntity<UserResponse> response = userController.registerUser(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals("John", response.getBody().getName());
    }

    @Test
    void getUser_returnsUser() {
        UserResponse responseBody = UserResponse.builder()
                .id(1L)
                .name("Jane")
                .email("jane@example.com")
                .role(Role.CUSTOMER)
                .build();
        when(userService.getUserById(1L)).thenReturn(responseBody);

        ResponseEntity<UserResponse> response = userController.getUser(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().getId());
    }

    @Test
    void getAllUsers_returnsList() {
        List<UserResponse> list = Collections.emptyList();
        when(userService.getAllUsers()).thenReturn(list);

        ResponseEntity<List<UserResponse>> response = userController.getAllUsers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    void deleteUserById_whenExists_returnsNoContent() {
        when(userService.deleteUserById(1L)).thenReturn(true);

        ResponseEntity<Void> response = userController.deleteUserById(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void deleteUserById_whenNotExists_returnsNotFound() {
        when(userService.deleteUserById(999L)).thenReturn(false);

        ResponseEntity<Void> response = userController.deleteUserById(999L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
