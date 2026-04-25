package com.classhub.service;

import com.classhub.dto.LoginRequest;
import com.classhub.dto.RegisterRequest;
import com.classhub.model.User;
import com.classhub.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) { this.userRepository = userRepository; }

    public User register(RegisterRequest request) {
        userRepository.findByEmail(request.getEmail()).ifPresent(u -> {
            throw new IllegalArgumentException("Email is already registered.");
        });
        User user = new User(null, request.getFullName(), request.getEmail(),
                request.getPassword(), request.getRole());
        return userRepository.save(user);
    }

    public User login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));
        if (!user.getPassword().equals(request.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password.");
        }
        return user;
    }
}
