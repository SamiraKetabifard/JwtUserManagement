package com.example.jwtusermanagement.controller;

import com.example.jwtusermanagement.entity.Role;
import com.example.jwtusermanagement.entity.User;
import com.example.jwtusermanagement.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    private User testUser;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("password");
        testUser.setRole(Role.USER);
    }

    @Test
    void createUser_Success() {
        when(userService.saveUser(any(User.class))).thenReturn(testUser);

        User response = userController.createUser(testUser);

        assertNotNull(response);
        assertEquals("testuser", response.getUsername());
    }

    @Test
    void getUserById_Success() {
        when(userService.getUser(1L)).thenReturn(testUser);

        User response = userController.getUserById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
    }

    @Test
    void getAllUsers_Success() {
        List<User> users = Arrays.asList(testUser, testUser);
        when(userService.getAllUsers()).thenReturn(users);

        List<User> response = userController.getAllUsers();

        assertNotNull(response);
        assertEquals(2, response.size());
    }

    @Test
    void updateUser_Success() {
        User updatedUser = new User();
        updatedUser.setUsername("updateduser");

        when(userService.saveUser(any(User.class))).thenReturn(updatedUser);

        User response = userController.updateUser(1L, updatedUser);

        assertNotNull(response);
        assertEquals("updateduser", response.getUsername());
    }

    @Test
    void deleteUser_Success() {
        doNothing().when(userService).deleteUser(1L);

        userController.deleteUser(1L);

        verify(userService, times(1)).deleteUser(1L);
    }
}