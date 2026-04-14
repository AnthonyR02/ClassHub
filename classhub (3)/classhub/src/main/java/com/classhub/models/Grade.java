package com.classhub.models;

public class Grade {
    private String subject, assignmentName, date;
    private double score, maxScore;

    public Grade(String subject, String assignmentName, String date, double score, double maxScore) {
        this.subject = subject; this.assignmentName = assignmentName;
        this.date = date; this.score = score; this.maxScore = maxScore;
    }

    public String getSubject()        { return subject; }
    public String getAssignmentName() { return assignmentName; }
    public String getDate()           { return date; }
    public double getScore()          { return score; }
    public double getMaxScore()       { return maxScore; }
    public double getPercentage()     { return maxScore == 0 ? 0 : (score / maxScore * 100); }
    public String getLetterGrade() {
        double p = getPercentage();
        if (p >= 90) return "A"; if (p >= 80) return "B";
        if (p >= 70) return "C"; if (p >= 60) return "D"; return "F";
    }
}
