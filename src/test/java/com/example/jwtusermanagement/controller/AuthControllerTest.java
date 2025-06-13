package com.example.jwtusermanagement.controller;

import com.example.jwtusermanagement.entity.User;
import com.example.jwtusermanagement.jwt.JwtUtil;
import com.example.jwtusermanagement.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Map;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthController authController;

    @Test
    void registerUser_NewUser_ReturnsRegisteredUser() {
        // Arrange
        User user = new User();
        user.setUsername("samira@gmail.com");
        user.setPassword("password123");
        when(userRepository.findByUsername("samira@gmail.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(1L);
            return u;
        });
        // Act
        ResponseEntity<?> response = authController.registerUser(user);
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof User);
        User registeredUser = (User) response.getBody();
        assertEquals(1L, registeredUser.getId());
        assertEquals("samira@gmail.com", registeredUser.getUsername());
        assertEquals("encodedPassword", registeredUser.getPassword());
    }
    @Test
    void registerUser_ExistingUsername_ReturnsBadRequest() {
        // Arrange
        User existingUser = new User(1L, "samira@gmail.com", "password", null);
        User newUser = new User();
        newUser.setUsername("samira@gmail.com");
        newUser.setPassword("password123");
        when(userRepository.findByUsername("samira@gmail.com")).thenReturn(Optional.of(existingUser));
        // Act
        ResponseEntity<?> response = authController.registerUser(newUser);
        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Username already exists", response.getBody());
    }
    @Test
    void loginUser_ValidCredentials_ReturnsToken() {
        // Arrange
        User user = new User();
        user.setUsername("samira@gmail.com");
        user.setPassword("password123");
        Authentication authentication = mock(Authentication.class);
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtUtil.generateToken("samira@gmail.com")).thenReturn("testToken");
        // Act
        ResponseEntity<?> response = authController.loginUser(user);
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof Map);
        Map<?, ?> responseBody = (Map<?, ?>) response.getBody();
        assertEquals("testToken", responseBody.get("token"));
    }
    @Test
    void loginUser_InvalidCredentials_ReturnsUnauthorized() {
        // Arrange
        User user = new User();
        user.setUsername("invalid@gmail.com");
        user.setPassword("wrongPassword");
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));
        // Act
        ResponseEntity<?> response = authController.loginUser(user);
        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals("Invalid username or password", response.getBody());
    }
}
