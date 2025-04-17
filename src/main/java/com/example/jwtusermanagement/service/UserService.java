package com.example.jwtusermanagement.service;

import com.example.jwtusermanagement.entity.User;

import java.util.List;

public interface UserService {

    User saveUser(User user);
    void deleteUser(long id);
    User getUser(long id);
    List<User> getAllUsers();

}
