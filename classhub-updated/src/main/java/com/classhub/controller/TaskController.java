package com.classhub.controller;

import com.classhub.dto.AssignmentRequest;
import com.classhub.model.Assignment;
import com.classhub.security.RequireRole;
import com.classhub.service.TaskService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/assignments")
@CrossOrigin(origins = "*")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * TEACHER / ADMIN only — assign work to students.
     * POST /api/assignments
     */
    @PostMapping
    @RequireRole({"TEACHER", "ADMIN"})
    public Assignment createAssignment(@Valid @RequestBody AssignmentRequest request) {
        return taskService.createAssignment(request);
    }

    /**
     * Students can only see their own assignments.
     * Teachers / Admins can see any user's assignments.
     * GET /api/assignments/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public List<Assignment> getAssignmentsByUser(@PathVariable String userId,
                                                  HttpServletRequest request) {
        String callerUid  = (String) request.getAttribute("uid");
        String callerRole = (String) request.getAttribute("role");

        if ("STUDENT".equals(callerRole) && !callerUid.equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                "Students can only view their own assignments.");
        }

        return taskService.getAssignmentsByUser(userId);
    }

    /**
     * Students mark their own assignments complete.
     * Teachers / Admins can mark any assignment complete.
     * PATCH /api/assignments/{assignmentId}/complete
     */
    @PatchMapping("/{assignmentId}/complete")
    public Assignment markCompleted(@PathVariable String assignmentId,
                                    HttpServletRequest request) {
        String callerRole = (String) request.getAttribute("role");
        String callerUid  = (String) request.getAttribute("uid");

        // Teachers and admins can complete any assignment
        if ("TEACHER".equals(callerRole) || "ADMIN".equals(callerRole)) {
            return taskService.markCompleted(assignmentId);
        }

        // Students can only complete their own assignments
        Assignment assignment = taskService.getAssignmentById(assignmentId);
        if (!callerUid.equals(assignment.getUserId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                "You can only mark your own assignments as complete.");
        }

        return taskService.markCompleted(assignmentId);
    }
}
