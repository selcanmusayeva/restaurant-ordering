package com.ordering.restaurant.service;

import com.ordering.restaurant.dto.AuthRequest;
import com.ordering.restaurant.dto.AuthResponse;
import com.ordering.restaurant.dto.RegisterRequest;
import com.ordering.restaurant.model.User;
import com.ordering.restaurant.model.UserRole;
import com.ordering.restaurant.repository.UserRepository;
import com.ordering.restaurant.security.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private AuthRequest authRequest;
    private User testUser;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setFullName("Test User");
        registerRequest.setUsername("testuser");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");

        authRequest = new AuthRequest("testuser", "password123");

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setRole(UserRole.WAITER);
    }

    @Test
    void registerUser_ValidRequest_ReturnsAuthResponse() {
        // Given
        when(userRepository.existsByUsername(any())).thenReturn(false);
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        when(userRepository.save(any())).thenReturn(testUser);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(testUser);
        when(jwtUtils.generateToken(any())).thenReturn("test.jwt.token");

        // When
        AuthResponse response = authService.registerUser(registerRequest);

        // Then
        assertNotNull(response);
        assertEquals("test.jwt.token", response.getToken());
        assertEquals("testuser", response.getUsername());
        assertEquals(UserRole.WAITER.name(), response.getRole());

        verify(userRepository).save(any());
    }

    @Test
    void registerUser_DuplicateUsername_ThrowsException() {
        // Given
        when(userRepository.existsByUsername(any())).thenReturn(true);

        // When & Then
        assertThrows(RuntimeException.class, () -> authService.registerUser(registerRequest));
    }

    @Test
    void registerUser_DuplicateEmail_ThrowsException() {
        // Given
        when(userRepository.existsByUsername(any())).thenReturn(false);
        when(userRepository.existsByEmail(any())).thenReturn(true);

        // When & Then
        assertThrows(RuntimeException.class, () -> authService.registerUser(registerRequest));
    }

    @Test
    void authenticateUser_ValidCredentials_ReturnsAuthResponse() {
        // Given
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(testUser);
        when(jwtUtils.generateToken(any())).thenReturn("test.jwt.token");

        // When
        AuthResponse response = authService.authenticateUser(authRequest);

        // Then
        assertNotNull(response);
        assertEquals("test.jwt.token", response.getToken());
        assertEquals("testuser", response.getUsername());
        assertEquals(UserRole.WAITER.name(), response.getRole());

        verify(authenticationManager).authenticate(any());
    }

    @Test
    void authenticateUser_InvalidCredentials_ThrowsException() {
        // Given
        when(authenticationManager.authenticate(any()))
                .thenThrow(new RuntimeException("Invalid credentials"));

        // When & Then
        assertThrows(RuntimeException.class, () -> authService.authenticateUser(authRequest));
    }
} 