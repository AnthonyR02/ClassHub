package com.classhub.models;

public class Assignment {
    private String title, subject, dueDate, priority;
    private boolean completed;

    public Assignment(String title, String subject, String dueDate, String priority, boolean completed) {
        this.title = title; this.subject = subject; this.dueDate = dueDate;
        this.priority = priority; this.completed = completed;
    }

    public String getTitle()     { return title; }
    public String getSubject()   { return subject; }
    public String getDueDate()   { return dueDate; }
    public String getPriority()  { return priority; }
    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean v) { this.completed = v; }
}
