package com.classhub.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class GradeRecordRequest {
    @NotBlank private String letterGrade;
    @NotNull @DecimalMin("0.0") @DecimalMax("4.0") private Double gradePoints;
    @NotBlank private String courseId;
    @NotBlank private String userId;

    public String getLetterGrade() { return letterGrade; }
    public void setLetterGrade(String v) { letterGrade = v; }
    public Double getGradePoints() { return gradePoints; }
    public void setGradePoints(Double v) { gradePoints = v; }
    public String getCourseId() { return courseId; }
    public void setCourseId(String v) { courseId = v; }
    public String getUserId() { return userId; }
    public void setUserId(String v) { userId = v; }
}
