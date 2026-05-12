package com.classhub.service;

import com.classhub.dto.LoginRequest;
import com.classhub.dto.RegisterRequest;
import com.classhub.model.Role;
import com.classhub.model.User;
import com.classhub.repository.UserRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.UserRecord;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Creates a new user in Firebase Authentication AND saves their profile to Firestore.
     * The user's role is embedded as a Firebase custom claim so every ID token
     * the client receives will carry the role — no extra Firestore lookup needed
     * during request authorization.
     */
    public User register(RegisterRequest request) throws FirebaseAuthException {
        // 1. Create the user in Firebase Auth
        UserRecord.CreateRequest createRequest = new UserRecord.CreateRequest()
                .setEmail(request.getEmail())
                .setPassword(request.getPassword())
                .setDisplayName(request.getFullName());

        UserRecord firebaseUser = FirebaseAuth.getInstance().createUser(createRequest);

        // 2. Embed the role as a custom claim on the Firebase user
        //    This claim will appear in every ID token the client fetches after this point.
        Map<String, Object> claims = Map.of("role", request.getRole());
        FirebaseAuth.getInstance().setCustomUserClaims(firebaseUser.getUid(), claims);

        // 3. Save the profile (no password) to Firestore
        User user = new User(firebaseUser.getUid(), request.getFullName(),
                             request.getEmail(), Role.valueOf(request.getRole()));
        return userRepository.save(user);
    }

    /**
     * Verifies a Firebase ID token sent from the client after they sign in.
     * Returns the Firestore user profile plus the role decoded from the token claims.
     */
    public User verifyToken(String idToken) throws FirebaseAuthException {
        FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
        String uid = decodedToken.getUid();

        User user = userRepository.findById(uid)
                .orElseThrow(() -> new IllegalArgumentException("User profile not found for UID: " + uid));

        // Ensure the in-memory role matches the claim (handles edge cases where
        // an admin updated the role directly in Firebase Console)
        Object roleClaim = decodedToken.getClaims().get("role");
        if (roleClaim != null) {
            user.setRole(roleClaim.toString());
        }

        return user;
    }

    /** @deprecated Login is handled client-side via the Firebase client SDK. */
    @Deprecated
    public User login(LoginRequest request) {
        throw new UnsupportedOperationException(
            "Login is handled on the client via the Firebase client SDK. " +
            "Send the resulting ID token to POST /api/auth/verify instead."
        );
    }
}
