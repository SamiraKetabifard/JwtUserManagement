package com.example.jwtusermanagement.controller;

import com.example.jwtusermanagement.entity.User;
import com.example.jwtusermanagement.service.UserService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

  @RestController
  @RequestMapping("/api/user")
  public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
}
//need jwt
  @PostMapping("/add")
  public User createUser (@RequestBody User user) {
    return userService.saveUser(user);
}
//just admin
  @DeleteMapping("/del/{id}")
  public void deleteUser (@PathVariable long id) {
    userService.deleteUser(id);
}
  @GetMapping("/get/{id}")
  public User getUserById(@PathVariable long id) {
    return userService.getUser(id);
}
  @GetMapping ("/getall")
  public List<User> getAllUsers() {
    return userService.getAllUsers();
}
//need jwt
  @PutMapping("/update/{id}")
  public User updateUser (@PathVariable Long id, @RequestBody User user) {
    user.setId(id);
    return userService.saveUser(user);
}
}
