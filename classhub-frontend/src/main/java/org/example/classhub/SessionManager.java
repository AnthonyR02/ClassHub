package org.example.classhub;

public class SessionManager {

    private static String userId;
    private static String fullName;
    private static String role;
    private static String email;
    private static String idToken;
    private static java.util.List<String[]> courses = new java.util.ArrayList<>();

    public static void login(String uid, String name, String userRole, String userEmail) {
        userId   = uid;
        fullName = name;
        role     = userRole;
        email    = userEmail;
    }

    public static void logout() {
        userId   = null;
        fullName = null;
        role     = null;
        email    = null;
        idToken = null;
        courses  = new java.util.ArrayList<>();
    }

    public static String getUserId()  {
        return userId;
    }
    public static String getFullName(){
        return fullName;
    }
    public static String getRole()    {
        return role;
    }
    public static String getEmail()   {
        return email;
    }
    public static void setIdToken(String token) {
        idToken = token;
    }
    public static String getIdToken() {
        return idToken;
    }
    public static void setCourses(java.util.List<String[]> c) { courses = c; }
    public static java.util.List<String[]> getCourses() { return courses; }

    public static String getInitials() {
        if (fullName == null || fullName.isBlank())
            return "?";
        String[] parts = fullName.trim().split(" ");
        if (parts.length == 1)
            return parts[0].substring(0, 1).toUpperCase();

        return (parts[0].substring(0, 1) + parts[parts.length - 1].substring(0, 1)).toUpperCase();
    }




}