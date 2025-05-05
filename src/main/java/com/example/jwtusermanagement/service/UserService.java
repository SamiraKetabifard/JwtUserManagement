package com.example.jwtusermanagement.service;
import com.example.jwtusermanagement.entity.User;
import java.util.List;

public interface UserService {

    User saveUser(User user);
    User getUser(Long id);
    List<User> getAllUsers();
    void deleteUser(Long id);

}
