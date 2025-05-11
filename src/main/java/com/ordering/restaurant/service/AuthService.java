package com.ordering.restaurant.service;

import com.ordering.restaurant.dto.AuthRequest;
import com.ordering.restaurant.dto.AuthResponse;
import com.ordering.restaurant.dto.RegisterRequest;
import com.ordering.restaurant.dto.UserDTO;
import com.ordering.restaurant.model.User;
import com.ordering.restaurant.model.UserRole;
import com.ordering.restaurant.repository.UserRepository;
import com.ordering.restaurant.security.JwtUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(AuthenticationManager authenticationManager,
                      JwtUtils jwtUtils,
                      UserRepository userRepository,
                      PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponse registerUser(RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new RuntimeException("Username is already taken");
        }

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Email is already in use");
        }

        User user = new User();
        user.setFullName(registerRequest.getFullName());
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(UserRole.CUSTOMER);
        user.setEnabled(true);

        userRepository.save(user);

        return authenticateUser(new AuthRequest(registerRequest.getUsername(), registerRequest.getPassword()));
    }

    public AuthResponse authenticateUser(AuthRequest authRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateToken((User) authentication.getPrincipal());
        
        User user = (User) authentication.getPrincipal();
        return new AuthResponse(jwt, user.getUsername(), user.getRole().name());
    }
    
    public void logoutUser() {
        SecurityContextHolder.clearContext();
    }
    
    public UserDTO getCurrentUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return UserDTO.fromEntity(user);
    }
    
    public AuthResponse refreshToken(String refreshToken) {
        String username = jwtUtils.extractUsername(refreshToken);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (!jwtUtils.validateToken(refreshToken, user)) {
            throw new RuntimeException("Invalid refresh token");
        }
        
        String newToken = jwtUtils.generateToken(user);
        return new AuthResponse(newToken, user.getUsername(), user.getRole().name());
    }
} 