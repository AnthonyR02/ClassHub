package com.classhub.controller;

import com.classhub.model.Note;
import com.classhub.repository.NoteRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/notes")
@CrossOrigin(origins = "*")
public class NoteController {

    private final NoteRepository noteRepository;

    public NoteController(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    @GetMapping("/user/{userId}")
    public List<Note> getNotes(@PathVariable String userId,
                               HttpServletRequest request) {
        return noteRepository.findByUserId(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Note createNote(@RequestBody Note note,
                           HttpServletRequest request) {
        note.setUserId((String) request.getAttribute("uid"));
        note.setCreatedAt(Instant.now().toString());
        return noteRepository.save(note);
    }

    @PutMapping("/{noteId}")
    public Note updateNote(@PathVariable String noteId,
                           @RequestBody Note note,
                           HttpServletRequest request) {
        note.setId(noteId);
        note.setUserId((String) request.getAttribute("uid"));
        return noteRepository.save(note);
    }

    @DeleteMapping("/{noteId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteNote(@PathVariable String noteId) {
        noteRepository.deleteById(noteId);
    }
}