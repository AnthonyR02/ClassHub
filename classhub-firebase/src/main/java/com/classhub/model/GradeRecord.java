package com.classhub.model;

/** Plain POJO stored in Firestore collection "gradeRecords". */
public class GradeRecord {
    private String id;
    private String letterGrade;
    private double gradePoints;
    private String courseId;
    private String userId;

    public GradeRecord() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getLetterGrade() { return letterGrade; }
    public void setLetterGrade(String letterGrade) { this.letterGrade = letterGrade; }
    public double getGradePoints() { return gradePoints; }
    public void setGradePoints(double gradePoints) { this.gradePoints = gradePoints; }
    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
}
