package com.classhub.dto;

public class GpaSummaryResponse {
    private double gpa;          // renamed: frontend uses .path("gpa")
    private int courseCount;     // renamed: frontend uses .path("courseCount")
    private int totalCredits;

    public GpaSummaryResponse(double gpa, int courseCount, int totalCredits) {
        this.gpa = gpa;
        this.courseCount = courseCount;
        this.totalCredits = totalCredits;
    }

    public double getGpa() { return gpa; }
    public int getCourseCount() { return courseCount; }
    public int getTotalCredits() { return totalCredits; }
}
