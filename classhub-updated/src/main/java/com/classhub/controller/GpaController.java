package com.classhub.controller;

import com.classhub.dto.GpaSummaryResponse;
import com.classhub.dto.GradeRecordRequest;
import com.classhub.model.GradeRecord;
import com.classhub.repository.GradeRecordRepository;
import com.classhub.service.GpaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/gpa")
@CrossOrigin(origins = "*")
public class GpaController {

    private final GpaService gpaService;
    private final GradeRecordRepository gradeRecordRepository;

    public GpaController(GpaService gpaService, GradeRecordRepository gradeRecordRepository) {
        this.gpaService = gpaService;
        this.gradeRecordRepository = gradeRecordRepository;
    }

    /**
     * Get all grade records for a user (used by GradesPage to show per-course grades).
     * Students can only view their own. Teachers / Admins can view any user's.
     * GET /api/gpa/records/{userId}
     */
    @GetMapping("/records/{userId}")
    public List<GradeRecord> getGradeRecords(@PathVariable String userId,
                                              HttpServletRequest request) {
        String callerUid  = (String) request.getAttribute("uid");
        String callerRole = (String) request.getAttribute("role");
        if ("STUDENT".equals(callerRole) && !callerUid.equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                "Students can only view their own grade records.");
        }
        return gradeRecordRepository.findByUserId(userId);
    }

    /**
     * TEACHER / ADMIN only — post a grade for a student.
     * POST /api/gpa/records
     */
    @PostMapping("/records")
    public GradeRecord addGradeRecord(@Valid @RequestBody GradeRecordRequest request,
                                      HttpServletRequest httpRequest) {
        request.setUserId((String) httpRequest.getAttribute("uid"));
        return gpaService.addGradeRecord(request);
    }

    /**
     * Students can only view their own GPA summary.
     * Teachers / Admins can view any user's summary.
     * GET /api/gpa/summary/{userId}
     */
    @GetMapping("/summary/{userId}")
    public GpaSummaryResponse getGpaSummary(@PathVariable String userId,
                                             HttpServletRequest request) {
        String callerUid  = (String) request.getAttribute("uid");
        String callerRole = (String) request.getAttribute("role");

        if ("STUDENT".equals(callerRole) && !callerUid.equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                "Students can only view their own GPA summary.");
        }

        return gpaService.calculateGpa(userId);
    }
}
