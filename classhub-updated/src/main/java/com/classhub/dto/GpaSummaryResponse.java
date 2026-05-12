package com.classhub.dto;

public class GpaSummaryResponse {
    private double currentGpa;
    private int gradedCourses;
    private int totalCredits;

    public GpaSummaryResponse(double currentGpa, int gradedCourses, int totalCredits) {
        this.currentGpa = currentGpa;
        this.gradedCourses = gradedCourses;
        this.totalCredits = totalCredits;
    }

    public double getCurrentGpa() { return currentGpa; }
    public int getGradedCourses() { return gradedCourses; }
    public int getTotalCredits() { return totalCredits; }
}
