package com.classhub.models;

public class ClassSession {
    private String name, room, teacher, day, startTime, endTime, color;

    public ClassSession(String name, String room, String teacher, String day,
                        String startTime, String endTime, String color) {
        this.name = name; this.room = room; this.teacher = teacher;
        this.day = day; this.startTime = startTime; this.endTime = endTime; this.color = color;
    }

    public String getName()      { return name; }
    public String getRoom()      { return room; }
    public String getTeacher()   { return teacher; }
    public String getDay()       { return day; }
    public String getStartTime() { return startTime; }
    public String getEndTime()   { return endTime; }
    public String getColor()     { return color; }
}
