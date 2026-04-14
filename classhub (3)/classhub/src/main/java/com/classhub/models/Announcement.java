package com.classhub.models;

public class Announcement {
    private String title, body, date, priority;
    private boolean pinned;

    public Announcement(String title, String body, String date, String priority, boolean pinned) {
        this.title = title; this.body = body; this.date = date;
        this.priority = priority; this.pinned = pinned;
    }

    public String getTitle()    { return title; }
    public String getBody()     { return body; }
    public String getDate()     { return date; }
    public String getPriority() { return priority; }
    public boolean isPinned()   { return pinned; }
    public void setPinned(boolean v) { this.pinned = v; }
}
