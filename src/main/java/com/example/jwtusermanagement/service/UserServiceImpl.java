package com.example.jwtusermanagement.service;
import com.example.jwtusermanagement.entity.User;
import com.example.jwtusermanagement.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    @Override
    public User saveUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
    @Override
    public void deleteUser(Long idUser) {
        userRepository.deleteById(idUser);
    }
    @Override
    public User getUser(Long idUser) {
        return userRepository.findById(idUser).orElse(null);
    }
    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}

