package com.classhub.controller;

import com.classhub.dto.GpaSummaryResponse;
import com.classhub.dto.GradeRecordRequest;
import com.classhub.model.GradeRecord;
import com.classhub.service.GpaService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/gpa")
@CrossOrigin(origins = "*")
public class GpaController {
    private final GpaService gpaService;

    public GpaController(GpaService gpaService) { this.gpaService = gpaService; }

    @PostMapping("/records")
    public GradeRecord addGradeRecord(@Valid @RequestBody GradeRecordRequest request) {
        return gpaService.addGradeRecord(request);
    }

    @GetMapping("/summary/{userId}")
    public GpaSummaryResponse getGpaSummary(@PathVariable String userId) {
        return gpaService.calculateGpa(userId);
    }
}
