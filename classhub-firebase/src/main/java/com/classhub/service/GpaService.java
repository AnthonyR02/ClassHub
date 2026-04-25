package com.classhub.service;

import com.classhub.dto.GpaSummaryResponse;
import com.classhub.dto.GradeRecordRequest;
import com.classhub.model.GradeRecord;
import com.classhub.repository.CourseRepository;
import com.classhub.repository.GradeRecordRepository;
import com.classhub.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GpaService {
    private final GradeRecordRepository gradeRecordRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    public GpaService(GradeRecordRepository gradeRecordRepository,
                      CourseRepository courseRepository,
                      UserRepository userRepository) {
        this.gradeRecordRepository = gradeRecordRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
    }

    public GradeRecord addGradeRecord(GradeRecordRequest request) {
        courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new IllegalArgumentException("Course not found."));
        userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        GradeRecord r = new GradeRecord();
        r.setLetterGrade(request.getLetterGrade());
        r.setGradePoints(request.getGradePoints());
        r.setCourseId(request.getCourseId());
        r.setUserId(request.getUserId());
        return gradeRecordRepository.save(r);
    }

    public GpaSummaryResponse calculateGpa(String userId) {
        List<GradeRecord> records = gradeRecordRepository.findByUserId(userId);
        if (records.isEmpty()) return new GpaSummaryResponse(0.0, 0, 0);

        double totalQualityPoints = 0;
        int totalCredits = 0;
        for (GradeRecord r : records) {
            int credits = courseRepository.findById(r.getCourseId())
                    .map(c -> c.getCredits()).orElse(0);
            totalQualityPoints += r.getGradePoints() * credits;
            totalCredits += credits;
        }
        double gpa = totalCredits == 0 ? 0.0 : totalQualityPoints / totalCredits;
        return new GpaSummaryResponse(Math.round(gpa * 100.0) / 100.0, records.size(), totalCredits);
    }
}
