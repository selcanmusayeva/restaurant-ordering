package com.ordering.restaurant.security;

import com.ordering.restaurant.model.User;
import com.ordering.restaurant.model.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JwtUtilsTest {

    private JwtUtils jwtUtils;
    private User testUser;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
        ReflectionTestUtils.setField(jwtUtils, "secret", "testSecretKey123456789testSecretKey123456789testSecretKey123456789");
        ReflectionTestUtils.setField(jwtUtils, "expiration", 86400L); // 24 hours in seconds

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setRole(UserRole.WAITER);

        userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser");
    }

    @Test
    void generateToken_ValidUser_ReturnsToken() {
        // When
        String token = jwtUtils.generateToken(userDetails);

        // Then
        assertNotNull(token);
        assertTrue(token.split("\\.").length == 3); // JWT has 3 parts
    }

    @Test
    void validateToken_ValidToken_ReturnsTrue() {
        // Given
        String token = jwtUtils.generateToken(userDetails);

        // When
        boolean isValid = jwtUtils.validateToken(token, userDetails);

        // Then
        assertTrue(isValid);
    }

    @Test
    void validateToken_InvalidToken_ReturnsFalse() {
        // Given
        String invalidToken = "invalid.token.here";

        // When
        boolean isValid = jwtUtils.validateToken(invalidToken, userDetails);

        // Then
        assertFalse(isValid);
    }

    @Test
    void extractUsername_ValidToken_ReturnsUsername() {
        // Given
        String token = jwtUtils.generateToken(userDetails);

        // When
        String username = jwtUtils.extractUsername(token);

        // Then
        assertEquals(userDetails.getUsername(), username);
    }

    @Test
    void extractUsername_InvalidToken_ThrowsException() {
        // Given
        String invalidToken = "invalid.token.here";

        // When & Then
        assertThrows(Exception.class, () -> jwtUtils.extractUsername(invalidToken));
    }

    @Test
    void generateToken_DifferentUsers_GenerateDifferentTokens() {
        // Given
        UserDetails userDetails2 = mock(UserDetails.class);
        when(userDetails2.getUsername()).thenReturn("testuser2");

        // When
        String token1 = jwtUtils.generateToken(userDetails);
        String token2 = jwtUtils.generateToken(userDetails2);

        // Then
        assertNotEquals(token1, token2);
    }

    @Test
    void generateToken_SameUser_GenerateDifferentTokens() {
        // When
        String token1 = jwtUtils.generateToken(userDetails);
        String token2 = jwtUtils.generateToken(userDetails);

        // Then
        assertNotEquals(token1, token2); // Different timestamps
    }
} 