package com.classhub.dto;

import com.classhub.model.Assignment;
import java.util.List;

public class DashboardResponse {
    private final String studentName;
    private final double currentGpa;
    private final long totalCourses;
    private final long pendingAssignments;
    private final List<Assignment> upcomingAssignments;

    public DashboardResponse(String studentName, double currentGpa, long totalCourses,
                             long pendingAssignments, List<Assignment> upcomingAssignments) {
        this.studentName = studentName;
        this.currentGpa = currentGpa;
        this.totalCourses = totalCourses;
        this.pendingAssignments = pendingAssignments;
        this.upcomingAssignments = upcomingAssignments;
    }

    public String getStudentName() { return studentName; }
    public double getCurrentGpa() { return currentGpa; }
    public long getTotalCourses() { return totalCourses; }
    public long getPendingAssignments() { return pendingAssignments; }
    public List<Assignment> getUpcomingAssignments() { return upcomingAssignments; }
}
