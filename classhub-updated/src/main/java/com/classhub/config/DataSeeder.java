package com.classhub.config;

import com.classhub.model.*;
import com.classhub.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;

/**
 * Seeds demo data into Firestore on first startup.
 * Skipped if any users already exist.
 *
 * NOTE: Demo users here are Firestore profile records only.
 * To actually log in you must create matching accounts in Firebase Authentication
 * (Firebase Console → Authentication → Add user) using the same emails,
 * then register them via POST /api/auth/register so custom claims are set.
 */
@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedData(UserRepository userRepository,
                               CourseRepository courseRepository,
                               AssignmentRepository assignmentRepository,
                               GradeRecordRepository gradeRecordRepository) {
        return args -> {
            if (userRepository.count() > 0) return;

            // --- Student ---
            User student = new User("demo-student-uid", "Anthony Student",
                                    "student@classhub.com", Role.STUDENT);
            userRepository.save(student);

            // --- Teacher ---
            User teacher = new User("demo-teacher-uid", "Prof. Jane Smith",
                                    "teacher@classhub.com", Role.TEACHER);
            userRepository.save(teacher);

            // --- Admin ---
            User admin = new User("demo-admin-uid", "ClassHub Admin",
                                  "admin@classhub.com", Role.ADMIN);
            userRepository.save(admin);

            // --- Courses (owned by the teacher, enrolled by the student) ---
            Course c1 = courseRepository.save(
                    new Course(null, "CSC325", "Software Engineering", 3, "Spring 2026", student.getId()));
            Course c2 = courseRepository.save(
                    new Course(null, "CSC311", "Data Structures", 4, "Spring 2026", student.getId()));

            // --- Assignments (created by teacher, assigned to student) ---
            Assignment a1 = new Assignment();
            a1.setTitle("SRS Draft");
            a1.setDescription("Write first draft of IEEE SRS");
            a1.setDueDate(LocalDate.now().plusDays(3).toString());
            a1.setPriority(Priority.HIGH.name());
            a1.setCompleted(false);
            a1.setCourseId(c1.getId());
            a1.setUserId(student.getId());
            assignmentRepository.save(a1);

            Assignment a2 = new Assignment();
            a2.setTitle("Prototype Review");
            a2.setDescription("Prepare low-fidelity prototype screens");
            a2.setDueDate(LocalDate.now().plusDays(7).toString());
            a2.setPriority(Priority.MEDIUM.name());
            a2.setCompleted(false);
            a2.setCourseId(c1.getId());
            a2.setUserId(student.getId());
            assignmentRepository.save(a2);

            Assignment a3 = new Assignment();
            a3.setTitle("Tree Traversal Lab");
            a3.setDescription("Complete DFS and BFS practice");
            a3.setDueDate(LocalDate.now().plusDays(5).toString());
            a3.setPriority(Priority.HIGH.name());
            a3.setCompleted(false);
            a3.setCourseId(c2.getId());
            a3.setUserId(student.getId());
            assignmentRepository.save(a3);

            // --- Grades (posted by teacher) ---
            GradeRecord g1 = new GradeRecord();
            g1.setLetterGrade("A-");
            g1.setGradePoints(3.7);
            g1.setCourseId(c1.getId());
            g1.setUserId(student.getId());
            gradeRecordRepository.save(g1);

            GradeRecord g2 = new GradeRecord();
            g2.setLetterGrade("B+");
            g2.setGradePoints(3.3);
            g2.setCourseId(c2.getId());
            g2.setUserId(student.getId());
            gradeRecordRepository.save(g2);

            System.out.println("✅ Firestore seeded. Student UID: " + student.getId()
                + " | Teacher UID: " + teacher.getId());
        };
    }
}
