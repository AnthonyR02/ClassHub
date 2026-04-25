package com.classhub.controller;

import com.classhub.dto.AssignmentRequest;
import com.classhub.model.Assignment;
import com.classhub.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assignments")
@CrossOrigin(origins = "*")
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) { this.taskService = taskService; }

    @PostMapping
    public Assignment createAssignment(@Valid @RequestBody AssignmentRequest request) {
        return taskService.createAssignment(request);
    }

    @GetMapping("/user/{userId}")
    public List<Assignment> getAssignmentsByUser(@PathVariable String userId) {
        return taskService.getAssignmentsByUser(userId);
    }

    @PatchMapping("/{assignmentId}/complete")
    public Assignment markCompleted(@PathVariable String assignmentId) {
        return taskService.markCompleted(assignmentId);
    }
}
