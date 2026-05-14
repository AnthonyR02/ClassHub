package com.classhub.controller;

import com.classhub.dto.DashboardResponse;
import com.classhub.service.DashboardService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    /**
     * Students can only fetch their own dashboard.
     * Teachers and Admins can fetch any user's dashboard.
     * GET /api/dashboard/{userId}
     */
    @GetMapping("/{userId}")
    public DashboardResponse getDashboard(@PathVariable String userId,
                                          HttpServletRequest request) {
        String callerUid  = (String) request.getAttribute("uid");
        String callerRole = (String) request.getAttribute("role");

        if ("STUDENT".equals(callerRole) && !callerUid.equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                "Students can only view their own dashboard.");
        }

        return dashboardService.getDashboard(userId);
    }
}
