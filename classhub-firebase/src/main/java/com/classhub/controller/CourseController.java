package com.classhub.controller;

import com.classhub.dto.CourseRequest;
import com.classhub.model.Course;
import com.classhub.service.CourseService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@CrossOrigin(origins = "*")
public class CourseController {
    private final CourseService courseService;

    public CourseController(CourseService courseService) { this.courseService = courseService; }

    @PostMapping
    public Course createCourse(@Valid @RequestBody CourseRequest request) {
        return courseService.createCourse(request);
    }

    @GetMapping("/user/{userId}")
    public List<Course> getCoursesByUser(@PathVariable String userId) {
        return courseService.getCoursesByUser(userId);
    }
}
