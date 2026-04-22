package com.classhub.service;

import com.classhub.dto.DashboardResponse;
import com.classhub.model.Assignment;
import com.classhub.model.User;
import com.classhub.repository.AssignmentRepository;
import com.classhub.repository.CourseRepository;
import com.classhub.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class DashboardService {
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final AssignmentRepository assignmentRepository;
    private final GpaService gpaService;

    public DashboardService(UserRepository userRepository, CourseRepository courseRepository,
                            AssignmentRepository assignmentRepository, GpaService gpaService) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.assignmentRepository = assignmentRepository;
        this.gpaService = gpaService;
    }

    public DashboardResponse getDashboard(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        double gpa = gpaService.calculateGpa(userId).getCurrentGpa();
        String today = LocalDate.now().toString();
        List<Assignment> upcoming = assignmentRepository.findTop5UpcomingByUserId(userId, today);
        long pending = assignmentRepository.findByUserIdAndCompletedFalse(userId).size();
        long totalCourses = courseRepository.findByUserId(userId).size();

        return new DashboardResponse(user.getFullName(), gpa, totalCourses, pending, upcoming);
    }
}
