package com.example.jwtusermanagement.controller;

import com.example.jwtusermanagement.entity.Role;
import com.example.jwtusermanagement.entity.User;
import com.example.jwtusermanagement.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    void createUser_ValidUser_ReturnsCreatedUser() {
        // Arrange
        User user = new User();
        user.setUsername("samira@gmail.com");
        user.setPassword("password123");
        user.setRole(Role.USER);

        User savedUser = new User(1L, "samira@gmail.com", "encodedPassword", Role.USER);
        when(userService.saveUser(user)).thenReturn(savedUser);

        // Act
        User result = userController.createUser(user);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("samira@gmail.com", result.getUsername());
        verify(userService).saveUser(user);
    }

    @Test
    void getUserById_UserExists_ReturnsUser() {
        // Arrange
        User user = new User(1L, "samira@gmail.com", "password123", Role.ADMIN);
        when(userService.getUser(1L)).thenReturn(user);

        // Act
        User result = userController.getUserById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("samira@gmail.com", result.getUsername());
    }

    @Test
    void getAllUsers_ReturnsAllUsers() {
        // Arrange
        User user1 = new User(1L, "samira@gmail.com", "pass1", Role.USER);
        User user2 = new User(2L, "samira@gmail.com", "pass2", Role.ADMIN);
        List<User> users = Arrays.asList(user1, user2);
        when(userService.getAllUsers()).thenReturn(users);

        // Act
        List<User> result = userController.getAllUsers();

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.containsAll(users));
    }

    @Test
    void updateUser_ValidUser_ReturnsUpdatedUser() {
        // Arrange
        Long userId = 1L;
        User user = new User();
        user.setUsername("samira@gmail.com");
        user.setPassword("newPassword");
        user.setRole(Role.ADMIN);

        User updatedUser = new User(userId, "samira1@gmail.com", "encodedNewPassword", Role.ADMIN);
        when(userService.saveUser(any(User.class))).thenReturn(updatedUser);

        // Act
        User result = userController.updateUser(userId, user);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("samira1@gmail.com", result.getUsername());
        verify(userService).saveUser(any(User.class));
    }

    @Test
    void deleteUser_ValidId_DeletesUser() {
        // Arrange
        Long userId = 1L;
        doNothing().when(userService).deleteUser(userId);

        // Act
        userController.deleteUser(userId);

        // Assert
        verify(userService).deleteUser(userId);
    }
}
