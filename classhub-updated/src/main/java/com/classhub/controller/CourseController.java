package com.classhub.controller;

import com.classhub.dto.CourseRequest;
import com.classhub.model.Course;
import com.classhub.security.RequireRole;
import com.classhub.service.CourseService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@CrossOrigin(origins = "*")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    /**
     * TEACHER / ADMIN — create a course and enroll a student.
     * The teacher's UID is taken from the verified token, not the request body.
     * POST /api/courses
     */
    @PostMapping
    @RequireRole({"TEACHER", "ADMIN"})
    public Course createCourse(@Valid @RequestBody CourseRequest request,
                               HttpServletRequest httpRequest) {
        // Stamp the creator's UID from the verified token
        request.setTeacherId((String) httpRequest.getAttribute("uid"));
        return courseService.createCourse(request);
    }

    /**
     * STUDENT — get own enrolled courses.
     * TEACHER / ADMIN — get any student's courses.
     * GET /api/courses/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public List<Course> getCoursesByUser(@PathVariable String userId,
                                         HttpServletRequest request) {
        String callerUid  = (String) request.getAttribute("uid");
        String callerRole = (String) request.getAttribute("role");

        if ("STUDENT".equals(callerRole) && !callerUid.equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                "Students can only view their own courses.");
        }
        return courseService.getCoursesByUser(userId);
    }

    /**
     * TEACHER — get all courses they created.
     * ADMIN — can look up any teacher's courses.
     * GET /api/courses/teacher/{teacherId}
     */
    @GetMapping("/teacher/{teacherId}")
    @RequireRole({"TEACHER", "ADMIN"})
    public List<Course> getCoursesByTeacher(@PathVariable String teacherId,
                                             HttpServletRequest request) {
        String callerUid  = (String) request.getAttribute("uid");
        String callerRole = (String) request.getAttribute("role");

        if ("TEACHER".equals(callerRole) && !callerUid.equals(teacherId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                "Teachers can only view their own courses.");
        }
        return courseService.getCoursesByTeacher(teacherId);
    }
}
