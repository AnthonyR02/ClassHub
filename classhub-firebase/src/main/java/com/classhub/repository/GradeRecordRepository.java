package com.classhub.repository;

import com.classhub.model.GradeRecord;
import com.google.cloud.firestore.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Repository
public class GradeRecordRepository {

    private static final String COLLECTION = "gradeRecords";
    private final Firestore db;

    public GradeRecordRepository(Firestore db) { this.db = db; }

    public GradeRecord save(GradeRecord record) {
        if (record.getId() == null) {
            record.setId(UUID.randomUUID().toString());
        }
        try {
            db.collection(COLLECTION).document(record.getId()).set(record).get();
            return record;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Firestore save failed", e);
        }
    }

    public List<GradeRecord> findByUserId(String userId) {
        try {
            return db.collection(COLLECTION)
                    .whereEqualTo("userId", userId).get().get()
                    .getDocuments().stream()
                    .map(doc -> {
                        GradeRecord r = doc.toObject(GradeRecord.class);
                        assert r != null;
                        r.setId(doc.getId());
                        return r;
                    })
                    .collect(Collectors.toList());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException("Firestore query failed", e);
        }
    }
}
