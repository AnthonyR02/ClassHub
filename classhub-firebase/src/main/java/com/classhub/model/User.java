package com.classhub.model;

/** Plain POJO stored in Firestore collection "users". */
public class User {
    private String id;          // Firestore document ID
    private String fullName;
    private String email;
    private String password;    // plain-text — prototype only
    private String role;        // stored as string; use Role.valueOf() to convert

    public User() {}

    public User(String id, String fullName, String email, String password, Role role) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.role = role.name();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
