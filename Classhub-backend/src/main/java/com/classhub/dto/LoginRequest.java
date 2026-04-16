package com.classhub.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/*
 * LoginRequest is a Data Transfer Object (DTO) used to
 * receive login information from the client.
 * It only contains the fields required for authentication.
 */
public class LoginRequest {

    /*
     * Stores the user's email address.
     * @Email ensures the value follows a valid email format.
     * @NotBlank ensures the field is not empty or null.
     */
    @Email
    @NotBlank
    private String email;

    /*
     * Stores the user's password.
     * @NotBlank ensures a password is provided.
     */
    @NotBlank
    private String password;

    /*
     * Returns the user's email.
     */
    public String getEmail() {
        return email;
    }

    /*
     * Sets the user's email.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /*
     * Returns the user's password.
     */
    public String getPassword() {
        return password;
    }

    /*
     * Sets the user's password.
     */
    public void setPassword(String password) {
        this.password = password;
    }
}