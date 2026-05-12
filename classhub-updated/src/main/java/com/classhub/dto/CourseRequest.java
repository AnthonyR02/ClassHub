package com.classhub.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CourseRequest {
    @NotBlank private String courseCode;
    @NotBlank private String courseName;
    @Min(1)   private int credits;
    @NotBlank private String semester;
    @NotBlank private String userId;     // student being enrolled
    private String teacherId;            // teacher creating the course (set from token in service)

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String v) { this.courseCode = v; }
    public String getCourseName() { return courseName; }
    public void setCourseName(String v) { this.courseName = v; }
    public int getCredits() { return credits; }
    public void setCredits(int v) { this.credits = v; }
    public String getSemester() { return semester; }
    public void setSemester(String v) { this.semester = v; }
    public String getUserId() { return userId; }
    public void setUserId(String v) { this.userId = v; }
    public String getTeacherId() { return teacherId; }
    public void setTeacherId(String v) { this.teacherId = v; }
}
