package com.classhub.model;

/** Plain POJO stored in Firestore collection "assignments". */
public class Assignment {
    private String id;
    private String title;
    private String description;
    private String dueDate;     // ISO-8601 date string, e.g. "2026-05-01"
    private String priority;
    private boolean completed;
    private String courseId;
    private String userId;

    public Assignment() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getDueDate() { return dueDate; }
    public void setDueDate(String dueDate) { this.dueDate = dueDate; }
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }
    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
}
