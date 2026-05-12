package com.classhub.controller;

import com.classhub.dto.GpaSummaryResponse;
import com.classhub.dto.GradeRecordRequest;
import com.classhub.model.GradeRecord;
import com.classhub.security.RequireRole;
import com.classhub.service.GpaService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/gpa")
@CrossOrigin(origins = "*")
public class GpaController {

    private final GpaService gpaService;

    public GpaController(GpaService gpaService) {
        this.gpaService = gpaService;
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
