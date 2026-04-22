package com.classhub.repository;

import com.classhub.model.User;
import com.google.cloud.firestore.*;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Repository
public class UserRepository {

    private static final String COLLECTION = "users";
    private final Firestore db;

    public UserRepository(Firestore db) { this.db = db; }

    public User save(User user) {
        if (user.getId() == null) {
            user.setId(UUID.randomUUID().toString());
        }
        try {
            db.collection(COLLECTION).document(user.getId()).set(user).get();
            return user;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Firestore save failed", e);
        }
    }

    public Optional<User> findById(String id) {
        try {
            DocumentSnapshot doc = db.collection(COLLECTION).document(id).get().get();
            if (!doc.exists()) return Optional.empty();
            User user = doc.toObject(User.class);
            assert user != null;
            user.setId(doc.getId());
            return Optional.of(user);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Firestore read failed", e);
        }
    }

    public Optional<User> findByEmail(String email) {
        try {
            QuerySnapshot snap = db.collection(COLLECTION)
                    .whereEqualTo("email", email).limit(1).get().get();
            if (snap.isEmpty()) return Optional.empty();
            DocumentSnapshot doc = snap.getDocuments().get(0);
            User user = doc.toObject(User.class);
            assert user != null;
            user.setId(doc.getId());
            return Optional.of(user);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Firestore query failed", e);
        }
    }

    public long count() {
        try {
            return db.collection(COLLECTION).get().get().size();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Firestore count failed", e);
        }
    }
}
