package com.classhub.models;

public class Note {
    private String title, subject, tag, body, date;

    public Note(String title, String subject, String tag, String body, String date) {
        this.title = title; this.subject = subject; this.tag = tag;
        this.body = body; this.date = date;
    }

    public String getTitle()   { return title; }
    public String getSubject() { return subject; }
    public String getTag()     { return tag; }
    public String getBody()    { return body; }
    public String getDate()    { return date; }
}
