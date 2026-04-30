package com.classhub.model;

/**
 * User profile stored in Firestore collection "users".
 * Credentials (password, tokens) are managed entirely by Firebase Authentication —
 * they are never stored here.
 */
public class User {
    private String id;          // Firebase UID — matches the Firestore document ID
    private String fullName;
    private String email;
    private String role;        // stored as string; use Role.valueOf() to convert

    public User() {}

    public User(String id, String fullName, String email, Role role) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.role = role.name();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
