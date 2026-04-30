package com.classhub.repository;

import com.classhub.model.Assignment;
import com.google.cloud.firestore.*;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Repository
public class AssignmentRepository {

    private static final String COLLECTION = "assignments";
    private final Firestore db;

    public AssignmentRepository(Firestore db) { this.db = db; }

    public Assignment save(Assignment assignment) {
        if (assignment.getId() == null) {
            assignment.setId(UUID.randomUUID().toString());
        }
        try {
            db.collection(COLLECTION).document(assignment.getId()).set(assignment).get();
            return assignment;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Firestore save failed", e);
        }
    }

    public java.util.Optional<Assignment> findById(String id) {
        try {
            DocumentSnapshot doc = db.collection(COLLECTION).document(id).get().get();
            if (!doc.exists()) return java.util.Optional.empty();
            Assignment a = doc.toObject(Assignment.class);
            assert a != null;
            a.setId(doc.getId());
            return java.util.Optional.of(a);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Firestore read failed", e);
        }
    }

    /** All assignments for a user, sorted by dueDate ascending. */
    public List<Assignment> findByUserIdOrderByDueDateAsc(String userId) {
        return fetchByUser(userId).stream()
                .sorted(Comparator.comparing(Assignment::getDueDate))
                .collect(Collectors.toList());
    }

    /** Incomplete assignments for a user, sorted by dueDate ascending. */
    public List<Assignment> findByUserIdAndCompletedFalse(String userId) {
        return fetchByUser(userId).stream()
                .filter(a -> !a.isCompleted())
                .sorted(Comparator.comparing(Assignment::getDueDate))
                .collect(Collectors.toList());
    }

    /** Up to 5 upcoming (dueDate >= today) assignments for the user. */
    public List<Assignment> findTop5UpcomingByUserId(String userId, String today) {
        return fetchByUser(userId).stream()
                .filter(a -> a.getDueDate().compareTo(today) >= 0)
                .sorted(Comparator.comparing(Assignment::getDueDate))
                .limit(5)
                .collect(Collectors.toList());
    }

    private List<Assignment> fetchByUser(String userId) {
        try {
            return db.collection(COLLECTION)
                    .whereEqualTo("userId", userId).get().get()
                    .getDocuments().stream()
                    .map(doc -> {
                        Assignment a = doc.toObject(Assignment.class);
                        assert a != null;
                        a.setId(doc.getId());
                        return a;
                    })
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Firestore query failed", e);
        }
    }
}
