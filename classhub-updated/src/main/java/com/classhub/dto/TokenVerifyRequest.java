package com.classhub.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Request body for POST /api/auth/verify
 * The client sends the Firebase ID token it received after signing in.
 */
public class TokenVerifyRequest {

    @NotBlank
    private String idToken;

    public String getIdToken() { return idToken; }
    public void setIdToken(String idToken) { this.idToken = idToken; }
}
