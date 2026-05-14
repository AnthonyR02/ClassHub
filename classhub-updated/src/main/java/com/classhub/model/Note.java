package com.classhub.model;

public class Note {
    private String id;
    private String userId;
    private String title;
    private String content;
    private String createdAt;

    public Note() {}
    public Note(String id, String userId, String title, String content, String createdAt) {
        this.id = id; this.userId = userId; this.title = title;
        this.content = content; this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}