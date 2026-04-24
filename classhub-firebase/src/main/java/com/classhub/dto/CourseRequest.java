package com.classhub.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CourseRequest {
    @NotBlank private String courseCode;
    @NotBlank private String courseName;
    @NotNull @Min(1) private Integer credits;
    @NotBlank private String semester;
    @NotBlank private String userId;   // Firestore doc ID (String)

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String v) { courseCode = v; }
    public String getCourseName() { return courseName; }
    public void setCourseName(String v) { courseName = v; }
    public Integer getCredits() { return credits; }
    public void setCredits(Integer v) { credits = v; }
    public String getSemester() { return semester; }
    public void setSemester(String v) { semester = v; }
    public String getUserId() { return userId; }
    public void setUserId(String v) { userId = v; }
}
