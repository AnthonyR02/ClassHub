package com.classhub.service;

import com.classhub.dto.LoginRequest;
import com.classhub.dto.LoginResponse;
import com.classhub.dto.RegisterRequest;
import com.classhub.model.Role;
import com.classhub.model.User;
import com.classhub.repository.UserRepository;
import com.classhub.security.JwtUtil;
import com.classhub.security.PasswordUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil        tokenStore;

    public AuthService(UserRepository userRepository, JwtUtil tokenStore) {
        this.userRepository = userRepository;
        this.tokenStore     = tokenStore;
    }

    /**
     * Register: create Firebase Auth account (so email is tracked),
     * store hashed password in Firestore, issue session token.
     */
    public LoginResponse register(RegisterRequest request) throws FirebaseAuthException {
        String role = request.getRole() != null
                ? request.getRole().trim().toUpperCase() : "STUDENT";

        // Create the Firebase Auth account (for email tracking only)
        UserRecord.CreateRequest createRequest = new UserRecord.CreateRequest()
                .setEmail(request.getEmail())
                .setPassword(request.getPassword())
                .setDisplayName(request.getFullName());

        UserRecord firebaseUser = FirebaseAuth.getInstance().createUser(createRequest);
        FirebaseAuth.getInstance().setCustomUserClaims(
                firebaseUser.getUid(), Map.of("role", role));

        // Save profile + hashed password to Firestore
        User user = new User(firebaseUser.getUid(), request.getFullName(),
                             request.getEmail(), Role.valueOf(role));
        user.setPasswordHash(PasswordUtil.hash(request.getPassword()));
        userRepository.save(user);

        String token = tokenStore.generateToken(firebaseUser.getUid(), role);
        return new LoginResponse(token, user);
    }

    /**
     * Login: look up user by email in Firestore, verify password hash locally.
     * Zero Firebase REST calls — no rate limits possible.
     */
    public LoginResponse login(LoginRequest request) {
        // Find user by email in Firestore
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password."));

        // Verify password against stored hash
        if (user.getPasswordHash() == null ||
                !PasswordUtil.verify(request.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password.");
        }

        String token = tokenStore.generateToken(user.getId(), user.getRole());
        return new LoginResponse(token, user);
    }

    /**
     * Validate a session token and return the user profile.
     */
    public User verifyToken(String token) {
        String[] data = tokenStore.validateToken(token);
        String uid    = data[0];
        String role   = data[1];
        User user = userRepository.findById(uid)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
        user.setRole(role);
        return user;
    }
}
