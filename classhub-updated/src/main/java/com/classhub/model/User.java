package com.classhub.model;

public class User {
    private String id;
    private String fullName;
    private String email;
    private String role;
    private String passwordHash; // SHA-256 hash — never returned to client

    public User() {}

    public User(String id, String fullName, String email, Role role) {
        this.id       = id;
        this.fullName = fullName;
        this.email    = email;
        this.role     = role.name();
    }

    public String getId()           { return id; }
    public void   setId(String id)  { this.id = id; }
    public String getFullName()     { return fullName; }
    public void   setFullName(String v) { fullName = v; }
    public String getEmail()        { return email; }
    public void   setEmail(String v)    { email = v; }
    public String getRole()         { return role; }
    public void   setRole(String v)     { role = v; }

    // passwordHash is stored in Firestore but never serialized back to clients
    public String getPasswordHash()         { return passwordHash; }
    public void   setPasswordHash(String v) { passwordHash = v; }
}
