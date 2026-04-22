package com.classhub.dto;

import com.classhub.model.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AssignmentRequest {
    @NotBlank private String title;
    private String description;
    @NotBlank private String dueDate;   // ISO-8601 date string "YYYY-MM-DD"
    @NotNull private Priority priority;
    @NotBlank private String courseId;
    @NotBlank private String userId;

    public String getTitle() { return title; }
    public void setTitle(String v) { title = v; }
    public String getDescription() { return description; }
    public void setDescription(String v) { description = v; }
    public String getDueDate() { return dueDate; }
    public void setDueDate(String v) { dueDate = v; }
    public Priority getPriority() { return priority; }
    public void setPriority(Priority v) { priority = v; }
    public String getCourseId() { return courseId; }
    public void setCourseId(String v) { courseId = v; }
    public String getUserId() { return userId; }
    public void setUserId(String v) { userId = v; }
}
