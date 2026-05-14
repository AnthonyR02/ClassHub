package com.classhub.dto;

public class LoginRequest {
    private String email;
    private String password;
    public String getEmail()    { return email; }
    public void setEmail(String e) { email = e; }
    public String getPassword() { return password; }
    public void setPassword(String p) { password = p; }
}
