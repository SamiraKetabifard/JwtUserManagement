package com.example.jwtusermanagement.jwt;

import com.example.jwtusermanagement.security.CustomUserDetails;
import com.example.jwtusermanagement.entity.User;
import com.example.jwtusermanagement.entity.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();

        // Use ReflectionTestUtils to set private fields
        ReflectionTestUtils.setField(jwtUtil, "secretKey", "testsecretkeytestsecretkeytestsecretkey");
        ReflectionTestUtils.setField(jwtUtil, "expirationTime", 3600000L); // 1 hour

        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        user.setRole(Role.USER);
        userDetails = new CustomUserDetails(user);
    }

    @Test
    void generateToken_Success() {
        String token = jwtUtil.generateToken("testuser");
        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @Test
    void validateToken_Valid() {
        String token = jwtUtil.generateToken("testuser");
        assertTrue(jwtUtil.validateToken(token, userDetails));
    }

    @Test
    void validateToken_InvalidUser() {
        String token = jwtUtil.generateToken("testuser");
        User anotherUser = new User();
        anotherUser.setUsername("anotheruser");
        anotherUser.setPassword("password");
        UserDetails anotherUserDetails = new CustomUserDetails(anotherUser);

        assertFalse(jwtUtil.validateToken(token, anotherUserDetails));
    }

    @Test
    void extractUsername_Success() {
        String token = jwtUtil.generateToken("testuser");
        assertEquals("testuser", jwtUtil.extractUsername(token));
    }

    @Test
    void isTokenExpired_ValidToken() {
        String token = jwtUtil.generateToken("testuser");
        assertFalse(jwtUtil.isTokenExpired(token));
    }

}