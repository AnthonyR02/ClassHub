package com.classhub.service;

import com.classhub.dto.AssignmentRequest;
import com.classhub.model.Assignment;
import com.classhub.repository.AssignmentRepository;
import com.classhub.repository.CourseRepository;
import com.classhub.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    private final AssignmentRepository assignmentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    public TaskService(AssignmentRepository assignmentRepository,
                       CourseRepository courseRepository,
                       UserRepository userRepository) {
        this.assignmentRepository = assignmentRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
    }

    public Assignment createAssignment(AssignmentRequest request) {
        courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new IllegalArgumentException("Course not found."));
        userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        Assignment a = new Assignment();
        a.setTitle(request.getTitle());
        a.setDescription(request.getDescription());
        a.setDueDate(request.getDueDate());
        a.setPriority(request.getPriority().name());
        a.setCompleted(false);
        a.setCourseId(request.getCourseId());
        a.setUserId(request.getUserId());
        return assignmentRepository.save(a);
    }

    public List<Assignment> getAssignmentsByUser(String userId) {
        return assignmentRepository.findByUserIdOrderByDueDateAsc(userId);
    }

    public Assignment getAssignmentById(String assignmentId) {
        return assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new IllegalArgumentException("Assignment not found."));
    }

    public Assignment markCompleted(String assignmentId) {
        Assignment a = getAssignmentById(assignmentId);
        a.setCompleted(true);
        return assignmentRepository.save(a);
    }
}
