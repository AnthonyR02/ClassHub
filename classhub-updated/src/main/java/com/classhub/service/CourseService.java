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

    /** Teacher creates a course and enrolls a student. */
    public Course createCourse(CourseRequest request) {
        userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Student not found."));

        Course course = new Course(null, request.getCourseCode(), request.getCourseName(),
                request.getCredits(), request.getSemester(), request.getUserId());
        course.setTeacherId(request.getTeacherId()); // stamped from the controller using uid
        return courseRepository.save(course);
    }

    /** Student: their enrolled courses. */
    public List<Course> getCoursesByUser(String userId) {
        return courseRepository.findByUserId(userId);
    }

    /** Teacher: courses they created. */
    public List<Course> getCoursesByTeacher(String teacherId) {
        return courseRepository.findByTeacherId(teacherId);
    }
}
