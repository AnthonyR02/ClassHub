package com.classhub.repository;

import com.classhub.model.Note;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class NoteRepository {

    private Firestore db() {
        return FirestoreClient.getFirestore();
    }

    public Note save(Note note) {
        try {
            if (note.getId() == null) note.setId(UUID.randomUUID().toString());
            db().collection("notes").document(note.getId()).set(note).get();
            return note;
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    public List<Note> findByUserId(String userId) {
        try {
            List<Note> notes = new ArrayList<>();
            db().collection("notes").whereEqualTo("userId", userId)
                    .get().get().forEach(doc -> notes.add(doc.toObject(Note.class)));
            return notes;
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    public void deleteById(String id) {
        try { db().collection("notes").document(id).delete().get(); }
        catch (Exception e) { throw new RuntimeException(e); }
    }
}