package com.example.jwtusermanagement.service;

import com.example.jwtusermanagement.entity.Role;
import com.example.jwtusermanagement.entity.User;
import com.example.jwtusermanagement.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void saveUser_ValidUser_ReturnsSavedUser() {
        // Arrange
        User user = new User();
        user.setUsername("samira@gmail.com");
        user.setPassword("password123");
        user.setRole(Role.USER);

        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(1L);
            return u;
        });

        // Act
        User savedUser = userService.saveUser(user);

        // Assert
        assertNotNull(savedUser.getId());
        assertEquals("samira@gmail.com", savedUser.getUsername());
        assertEquals("encodedPassword", savedUser.getPassword());
        assertEquals(Role.USER, savedUser.getRole());
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(user);
    }

    @Test
    void getUser_UserExists_ReturnsUser() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("samira@gmail.com");
        user.setPassword("password123");
        user.setRole(Role.ADMIN);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act
        User foundUser = userService.getUser(1L);

        // Assert
        assertNotNull(foundUser);
        assertEquals(1L, foundUser.getId());
        assertEquals("samira@gmail.com", foundUser.getUsername());
    }

    @Test
    void getUser_UserNotExists_ReturnsNull() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        User foundUser = userService.getUser(1L);

        // Assert
        assertNull(foundUser);
    }

    @Test
    void getAllUsers_ReturnsAllUsers() {
        // Arrange
        User user1 = new User(1L, "samira@gmail.com", "pass1", Role.USER);
        User user2 = new User(2L, "samira@gmail.com", "pass2", Role.ADMIN);
        List<User> users = Arrays.asList(user1, user2);

        when(userRepository.findAll()).thenReturn(users);

        // Act
        List<User> allUsers = userService.getAllUsers();

        // Assert
        assertEquals(2, allUsers.size());
        assertTrue(allUsers.containsAll(users));
    }

    @Test
    void deleteUser_ValidId_DeletesUser() {
        // Arrange
        doNothing().when(userRepository).deleteById(1L);

        // Act
        userService.deleteUser(1L);

        // Assert
        verify(userRepository).deleteById(1L);
    }
}
