package com.classhub.security;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Password hashing using SHA-256 + salt.
 * No external libraries — uses Java built-ins only.
 */
public class PasswordUtil {

    public static String hash(String password) {
        try {
            // Generate random salt
            byte[] salt = new byte[16];
            new SecureRandom().nextBytes(salt);

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hashed = md.digest(password.getBytes("UTF-8"));

            // Store as "salt:hash" both base64-encoded
            return Base64.getEncoder().encodeToString(salt) + ":" +
                   Base64.getEncoder().encodeToString(hashed);
        } catch (Exception e) {
            throw new RuntimeException("Hashing failed", e);
        }
    }

    public static boolean verify(String password, String stored) {
        try {
            String[] parts = stored.split(":");
            byte[] salt    = Base64.getDecoder().decode(parts[0]);

            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] hashed = md.digest(password.getBytes("UTF-8"));

            return parts[1].equals(Base64.getEncoder().encodeToString(hashed));
        } catch (Exception e) {
            return false;
        }
    }
}
