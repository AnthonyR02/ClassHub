package com.classhub.dto;

import com.classhub.model.User;

public class RegisterResponse {
    private String customToken;
    private User   user;

    public RegisterResponse() {}

    public RegisterResponse(String customToken, User user) {
        this.customToken = customToken;
        this.user        = user;
    }

    public String getCustomToken() { return customToken; }
    public void   setCustomToken(String customToken) { this.customToken = customToken; }
    public User   getUser()        { return user; }
    public void   setUser(User user) { this.user = user; }
}
