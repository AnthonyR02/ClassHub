package com.classhub.service;

import com.classhub.dto.CourseRequest;
import com.classhub.model.Course;
import com.classhub.repository.CourseRepository;
import com.classhub.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseService {
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    public CourseService(CourseRepository courseRepository, UserRepository userRepository) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
    }

    public Course createCourse(CourseRequest request) {
        // Verify user exists
        userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found."));
        Course course = new Course(null, request.getCourseCode(), request.getCourseName(),
                request.getCredits(), request.getSemester(), request.getUserId());
        return courseRepository.save(course);
    }

    public List<Course> getCoursesByUser(String userId) {
        return courseRepository.findByUserId(userId);
    }
}
