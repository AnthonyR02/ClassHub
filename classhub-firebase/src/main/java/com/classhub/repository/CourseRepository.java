package com.classhub.repository;

import com.classhub.model.Course;
import com.google.cloud.firestore.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Repository
public class CourseRepository {

    private static final String COLLECTION = "courses";
    private final Firestore db;

    public CourseRepository(Firestore db) { this.db = db; }

    public Course save(Course course) {
        if (course.getId() == null) {
            course.setId(UUID.randomUUID().toString());
        }
        try {
            db.collection(COLLECTION).document(course.getId()).set(course).get();
            return course;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Firestore save failed", e);
        }
    }

    public Optional<Course> findById(String id) {
        try {
            DocumentSnapshot doc = db.collection(COLLECTION).document(id).get().get();
            if (!doc.exists()) return Optional.empty();
            Course c = doc.toObject(Course.class);
            assert c != null;
            c.setId(doc.getId());
            return Optional.of(c);
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Firestore read failed", e);
        }
    }

    public List<Course> findByUserId(String userId) {
        try {
            return db.collection(COLLECTION)
                    .whereEqualTo("userId", userId).get().get()
                    .getDocuments().stream()
                    .map(doc -> {
                        Course c = doc.toObject(Course.class);
                        assert c != null;
                        c.setId(doc.getId());
                        return c;
                    })
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Firestore query failed", e);
        }
    }
}
