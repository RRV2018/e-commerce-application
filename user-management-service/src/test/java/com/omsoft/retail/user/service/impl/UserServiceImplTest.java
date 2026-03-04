package com.omsoft.retail.user.service.impl;

import com.omsoft.retail.user.dto.UserRequest;
import com.omsoft.retail.user.dto.UserResponse;
import com.omsoft.retail.user.entiry.User;
import com.omsoft.retail.user.entiry.type.Role;
import com.omsoft.retail.user.exception.AlreadyExistsException;
import com.omsoft.retail.user.exception.UserNotFoundException;
import com.omsoft.retail.user.repository.UserRepository;
import com.omsoft.retail.user.util.EncryptDecryptUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EncryptDecryptUtil encryptDecryptUtil;

    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository, passwordEncoder, encryptDecryptUtil);
    }

    @Test
    void registerUser_whenEmailExists_throwsAlreadyExistsException() {
        UserRequest request = new UserRequest();
        request.setName("John");
        request.setEmail("existing@example.com");
        request.setPassword("pass123");
        when(userRepository.findByEmail("existing@example.com")).thenReturn(Optional.of(new User()));

        assertThrows(AlreadyExistsException.class, () -> userService.registerUser(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void registerUser_whenEmailNew_savesAndReturnsResponse() {
        UserRequest request = new UserRequest();
        request.setName("John");
        request.setEmail("new@example.com");
        request.setPassword("pass123");
        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(encryptDecryptUtil.encrypt(anyString())).thenReturn("encrypted");
        User savedUser = User.builder()
                .id(1L)
                .username("John")
                .email("new@example.com")
                .password("encoded")
                .decryptablePassword("encrypted")
                .role(Role.CUSTOMER)
                .addresses(Collections.emptyList())
                .build();
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(encryptDecryptUtil.decrypt("encrypted")).thenReturn("pass123");

        UserResponse response = userService.registerUser(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("John", response.getName());
        assertEquals("new@example.com", response.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void getUserById_whenNotFound_throws() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(999L));
    }

    @Test
    void deleteUserById_whenExists_returnsTrue() {
        when(userRepository.existsById(1L)).thenReturn(true);

        boolean result = userService.deleteUserById(1L);

        assertTrue(result);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUserById_whenNotExists_returnsFalse() {
        when(userRepository.existsById(999L)).thenReturn(false);

        boolean result = userService.deleteUserById(999L);

        assertFalse(result);
        verify(userRepository, never()).deleteById(any());
    }

    @Test
    void getAllUsers_returnsMappedList() {
        User user = User.builder()
                .id(1L)
                .username("U")
                .email("u@e.com")
                .decryptablePassword("enc")
                .role(Role.CUSTOMER)
                .addresses(Collections.emptyList())
                .build();
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserResponse> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }
}
