package com.classhub.models;

public class Attendance {
    private String studentName;
    private int present, absent, late;

    public Attendance(String studentName, int present, int absent, int late) {
        this.studentName = studentName; this.present = present;
        this.absent = absent; this.late = late;
    }

    public String getStudentName() { return studentName; }
    public int getPresent()        { return present; }
    public int getAbsent()         { return absent; }
    public int getLate()           { return late; }
    public int getTotal()          { return present + absent + late; }
    public double getRate()        { return getTotal() == 0 ? 0 : (present * 100.0 / getTotal()); }
}
