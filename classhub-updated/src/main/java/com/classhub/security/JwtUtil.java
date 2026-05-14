package com.classhub.security;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple in-memory session token store.
 * Generates a random token on login/register and maps it to uid + role.
 * No external dependencies needed.
 */
@Component
public class JwtUtil {

    // token -> { uid, role }
    private final Map<String, String[]> sessions = new ConcurrentHashMap<>();

    /** Generate a new session token for a user. */
    public String generateToken(String uid, String role) {
        String token = UUID.randomUUID().toString();
        sessions.put(token, new String[]{uid, role});
        return token;
    }

    /** Returns { uid, role } for a valid token, or throws if invalid. */
    public String[] validateToken(String token) {
        String[] data = sessions.get(token);
        if (data == null) throw new IllegalArgumentException("Invalid or expired session token.");
        return data;
    }

    /** Invalidate a token (logout). */
    public void invalidate(String token) {
        sessions.remove(token);
    }
}
