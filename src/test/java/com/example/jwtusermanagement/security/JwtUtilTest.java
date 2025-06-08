package com.example.jwtusermanagement.security;

import com.example.jwtusermanagement.jwt.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;
import java.security.Key;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private final String secretKey = Base64.getEncoder().encodeToString("test-secret-key-1234567890-1234567890".getBytes());
    private final long expirationTime = 1000 * 60 * 60; // 1 hour

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        // Inject values using reflection since they're normally set by Spring
        ReflectionTestUtils.setField(jwtUtil, "secretKey", secretKey);
        ReflectionTestUtils.setField(jwtUtil, "expirationTime", expirationTime);
    }

    @Test
    void generateToken_ValidUsername_ReturnsValidToken() {
        // Arrange
        String username = "testuser@example.com";

        // Act
        String token = jwtUtil.generateToken(username);

        // Assert
        assertNotNull(token);
        assertFalse(token.isEmpty());
        assertEquals(username, jwtUtil.extractUsername(token));
    }

    @Test
    void extractUsername_ValidToken_ReturnsCorrectUsername() {
        // Arrange
        String username = "testuser@example.com";
        String token = Jwts.builder()
                .setClaims(new HashMap<>())
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();

        // Act
        String extractedUsername = jwtUtil.extractUsername(token);

        // Assert
        assertEquals(username, extractedUsername);
    }

    @Test
    void validateToken_ValidTokenAndUserDetails_ReturnsTrue() {
        // Arrange
        String username = "validuser@example.com";
        String token = jwtUtil.generateToken(username);
        UserDetails userDetails = User.builder()
                .username(username)
                .password("password")
                .authorities(Collections.emptyList())
                .build();

        // Act
        boolean isValid = jwtUtil.validateToken(token, userDetails);

        // Assert
        assertTrue(isValid);
    }
    @Test
    void validateToken_ExpiredToken_ReturnsFalse() {
        // Arrange
        String username = "expireduser@example.com";
        String token = Jwts.builder()
                .setClaims(new HashMap<>())
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis() - expirationTime - 1000))
                .setExpiration(new Date(System.currentTimeMillis() - 1000))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();

        UserDetails userDetails = User.builder()
                .username(username)
                .password("password")
                .authorities(Collections.emptyList())
                .build();

        // Act
        boolean isValid = jwtUtil.validateToken(token, userDetails);

        // Assert
        assertFalse(isValid, "Expired token should be invalid");
    }
    @Test
    void validateToken_InvalidToken_ReturnsFalse() {
        // Arrange
        String invalidToken = "invalid.token.string";
        UserDetails userDetails = User.builder()
                .username("someuser@example.com")
                .password("password")
                .authorities(Collections.emptyList())
                .build();

        // Act & Assert
        assertFalse(jwtUtil.validateToken(invalidToken, userDetails));
    }

    @Test
    void validateToken_TokenWithDifferentUsername_ReturnsFalse() {
        // Arrange
        String username = "tokenuser@example.com";
        String differentUsername = "differentuser@example.com";
        String token = jwtUtil.generateToken(username);
        UserDetails userDetails = User.builder()
                .username(differentUsername)
                .password("password")
                .authorities(Collections.emptyList())
                .build();

        // Act
        boolean isValid = jwtUtil.validateToken(token, userDetails);

        // Assert
        assertFalse(isValid);
    }

    private Key getSignKey() {
        return Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKey));
    }
}