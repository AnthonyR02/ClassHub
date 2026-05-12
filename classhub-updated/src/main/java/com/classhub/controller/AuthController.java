package com.classhub.controller;

import com.classhub.dto.RegisterRequest;
import com.classhub.dto.TokenVerifyRequest;
import com.classhub.model.User;
import com.classhub.service.AuthService;
import com.google.firebase.auth.FirebaseAuthException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Register a new user.
     * Creates them in Firebase Auth and stores their profile in Firestore.
     *
     * POST /api/auth/register
     * Body: { fullName, email, password, role }
     */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public User register(@Valid @RequestBody RegisterRequest request) {
        try {
            return authService.register(request);
        } catch (FirebaseAuthException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Verify a Firebase ID token issued after client-side login.
     *
     * Flow:
     *   1. Client calls Firebase signInWithEmailAndPassword(email, password)
     *   2. Client gets an ID token from Firebase
     *   3. Client sends that token here
     *   4. Backend verifies it and returns the user profile
     *
     * POST /api/auth/verify
     * Body: { idToken: "<firebase-id-token>" }
     */
    @PostMapping("/verify")
    public User verifyToken(@RequestBody TokenVerifyRequest request) {
        try {
            return authService.verifyToken(request.getIdToken());
        } catch (FirebaseAuthException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired token.");
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
