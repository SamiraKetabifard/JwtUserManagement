package com.example.jwtusermanagement.repository;

import com.example.jwtusermanagement.entity.Role;
import com.example.jwtusermanagement.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByUsername_UserExists_ReturnsUser() {
        // Arrange
        User user = new User();
        user.setUsername("samira@gmail.com");
        user.setPassword("password123");
        user.setRole(Role.USER);
        userRepository.save(user);

        // Act
        Optional<User> foundUser = userRepository.findByUsername("samira@gmail.com");

        // Assert
        assertTrue(foundUser.isPresent());
        assertEquals("samira@gmail.com", foundUser.get().getUsername());
    }

    @Test
    void findByUsername_UserNotExists_ReturnsEmpty() {
        // Act
        Optional<User> foundUser = userRepository.findByUsername("nonexistent@gmail.com");

        // Assert
        assertFalse(foundUser.isPresent());
    }

    @Test
    void saveUser_ValidUser_ReturnsSavedUser() {
        // Arrange
        User user = new User();
        user.setUsername("samira@gmail.com");
        user.setPassword("password123");
        user.setRole(Role.ADMIN);

        // Act
        User savedUser = userRepository.save(user);

        // Assert
        assertNotNull(savedUser.getId());
        assertEquals("samira@gmail.com", savedUser.getUsername());
        assertEquals(Role.ADMIN, savedUser.getRole());
    }
}