package com.classhub.controller;

import com.classhub.dto.LoginRequest;
import com.classhub.dto.LoginResponse;
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

    /** POST /api/auth/register — create account, returns session token + user */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public LoginResponse register(@Valid @RequestBody RegisterRequest request) {
        try {
            return authService.register(request);
        } catch (FirebaseAuthException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    /** POST /api/auth/login — verify password locally, returns session token + user */
    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        try {
            return authService.login(request);
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    /** POST /api/auth/verify — validate session token, returns user profile */
    @PostMapping("/verify")
    public User verifyToken(@RequestBody TokenVerifyRequest request) {
        try {
            return authService.verifyToken(request.getIdToken());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }
}
