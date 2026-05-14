package com.classhub.model;

/** Plain POJO stored in Firestore collection "courses". */
public class Course {
    private String id;
    private String courseCode;
    private String courseName;
    private int credits;
    private String semester;
    private String userId;      // student enrolled in the course
    private String teacherId;   // teacher who created/owns the course

    public Course() {}

    public Course(String id, String courseCode, String courseName,
                  int credits, String semester, String userId) {
        this.id = id;
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.credits = credits;
        this.semester = semester;
        this.userId = userId;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public int getCredits() { return credits; }
    public void setCredits(int credits) { this.credits = credits; }
    public String getSemester() { return semester; }
    public void setSemester(String semester) { this.semester = semester; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getTeacherId() { return teacherId; }
    public void setTeacherId(String teacherId) { this.teacherId = teacherId; }
}
