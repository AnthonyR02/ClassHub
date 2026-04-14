package com.classhub.utils;

import com.classhub.models.*;
import java.util.ArrayList;
import java.util.List;

public class DataStore {
    private static final DataStore INSTANCE = new DataStore();
    public static DataStore getInstance() { return INSTANCE; }

    private final List<Assignment>   assignments   = new ArrayList<>();
    private final List<Attendance>   attendance    = new ArrayList<>();
    private final List<Announcement> announcements = new ArrayList<>();
    private final List<ClassSession> sessions      = new ArrayList<>();
    private final List<Grade>        grades        = new ArrayList<>();
    private final List<Note>         notes         = new ArrayList<>();

    private DataStore() { loadSampleData(); }

    private void loadSampleData() {
        // Assignments
        assignments.add(new Assignment("Calculus Problem Set 3",  "Mathematics",   "Apr 2, 2024",  "HIGH",   false));
        assignments.add(new Assignment("Essay: The Great Gatsby",  "English Lit",   "Apr 5, 2024",  "HIGH",   false));
        assignments.add(new Assignment("Lab Report – Titration",   "Chemistry",     "Apr 3, 2024",  "MEDIUM", false));
        assignments.add(new Assignment("History Presentation",     "World History", "Apr 8, 2024",  "MEDIUM", false));
        assignments.add(new Assignment("Physics Quiz Prep",        "Physics",       "Apr 1, 2024",  "LOW",    true));
        assignments.add(new Assignment("Spanish Vocab Review",     "Spanish",       "Mar 28, 2024", "LOW",    true));
        assignments.add(new Assignment("Art Portfolio Sketch",     "Art",           "Apr 10, 2024", "LOW",    false));
        assignments.add(new Assignment("Biology Diagram",          "Biology",       "Apr 6, 2024",  "MEDIUM", false));

        // Attendance (studentName, present, absent, late)
        attendance.add(new Attendance("Alice Chen",    42, 2, 1));
        attendance.add(new Attendance("Bob Martinez",  38, 6, 3));
        attendance.add(new Attendance("Carol Johnson", 44, 1, 0));
        attendance.add(new Attendance("David Kim",     35, 8, 2));
        attendance.add(new Attendance("Emma Wilson",   40, 3, 2));
        attendance.add(new Attendance("Frank Nguyen",  41, 2, 2));
        attendance.add(new Attendance("Grace Lee",     43, 1, 1));
        attendance.add(new Attendance("Henry Patel",   30, 12, 3));

        // Announcements (title, body, date, priority, pinned)
        announcements.add(new Announcement(
            "Midterm Exam Schedule Released",
            "Midterm exams will take place from April 15–19. Please review the schedule on the portal and notify your homeroom teacher of any conflicts as soon as possible.",
            "Mar 28, 2024", "HIGH", true));
        announcements.add(new Announcement(
            "Spring Break Reminder",
            "School will be closed April 12–14 for Spring Break. Classes resume on Monday, April 15. Enjoy the break!",
            "Mar 27, 2024", "MEDIUM", true));
        announcements.add(new Announcement(
            "Library Extended Hours",
            "The library will be open until 7 PM on weekdays through the end of the semester to support students during exam season.",
            "Mar 26, 2024", "LOW", false));
        announcements.add(new Announcement(
            "Science Fair Registration Open",
            "Registration for the annual Science Fair is now open. Submit your project proposal by April 5. See Mr. Hassan for details.",
            "Mar 25, 2024", "MEDIUM", false));
        announcements.add(new Announcement(
            "Cafeteria Menu Update",
            "New spring menu items will be available starting next Monday. Vegetarian and allergy-friendly options have been expanded.",
            "Mar 24, 2024", "LOW", false));

        // ClassSession (name, room, teacher, day, startTime, endTime, color)
        sessions.add(new ClassSession("Mathematics",   "Room 204", "Ms. Rivera",   "Monday",    "8:00",  "9:00",  "#6c8ef5"));
        sessions.add(new ClassSession("English Lit",   "Room 110", "Mr. Thompson", "Monday",    "9:15",  "10:15", "#3ecfb0"));
        sessions.add(new ClassSession("Chemistry",     "Lab 3",    "Dr. Patel",    "Monday",    "10:30", "11:30", "#f5697b"));
        sessions.add(new ClassSession("Physics",       "Room 301", "Mr. Okoro",    "Tuesday",   "8:00",  "9:00",  "#9f7ffe"));
        sessions.add(new ClassSession("World History", "Room 105", "Ms. Green",    "Tuesday",   "9:15",  "10:15", "#f5a623"));
        sessions.add(new ClassSession("Mathematics",   "Room 204", "Ms. Rivera",   "Wednesday", "8:00",  "9:00",  "#6c8ef5"));
        sessions.add(new ClassSession("Spanish",       "Room 202", "Señor López",  "Wednesday", "9:15",  "10:15", "#3ecfb0"));
        sessions.add(new ClassSession("Biology",       "Lab 1",    "Ms. Chen",     "Thursday",  "8:00",  "9:00",  "#f5697b"));
        sessions.add(new ClassSession("English Lit",   "Room 110", "Mr. Thompson", "Thursday",  "9:15",  "10:15", "#3ecfb0"));
        sessions.add(new ClassSession("Physics",       "Room 301", "Mr. Okoro",    "Friday",    "8:00",  "9:00",  "#9f7ffe"));
        sessions.add(new ClassSession("Chemistry",     "Lab 3",    "Dr. Patel",    "Friday",    "9:15",  "10:15", "#f5697b"));
        sessions.add(new ClassSession("Art",           "Studio",   "Ms. Walsh",    "Friday",    "10:30", "11:30", "#f5a623"));

        // Grade (subject, assignmentName, date, score, maxScore)
        grades.add(new Grade("Mathematics",   "Quiz 1",          "Mar 10", 88,  100));
        grades.add(new Grade("Mathematics",   "Problem Set 2",   "Mar 18", 92,  100));
        grades.add(new Grade("English Lit",   "Essay 1",         "Mar 5",  78,  100));
        grades.add(new Grade("English Lit",   "Reading Quiz",    "Mar 20", 84,  100));
        grades.add(new Grade("Chemistry",     "Lab Report 1",    "Mar 8",  90,  100));
        grades.add(new Grade("Chemistry",     "Midterm",         "Mar 22", 76,  100));
        grades.add(new Grade("Physics",       "Problem Set 1",   "Mar 12", 95,  100));
        grades.add(new Grade("World History", "Map Quiz",        "Mar 15", 82,  100));
        grades.add(new Grade("World History", "Essay",           "Mar 25", 88,  100));
        grades.add(new Grade("Spanish",       "Vocab Test",      "Mar 19", 91,  100));
        grades.add(new Grade("Biology",       "Lab Practical",   "Mar 21", 87,  100));

        // Note (title, subject, tag, body, date)
        notes.add(new Note("Derivatives – Chain Rule",
            "Mathematics", "Calculus",
            "The chain rule states that the derivative of a composite function f(g(x)) is f'(g(x)) · g'(x).\n\n" +
            "Example: d/dx[sin(x²)] = cos(x²) · 2x\n\n" +
            "Remember to work from the outside in. Always identify the outer and inner functions first before differentiating.",
            "Mar 28, 2024"));
        notes.add(new Note("Symbolism in The Great Gatsby",
            "English Lit", "Literature",
            "Key symbols:\n• The green light – Gatsby's hopes and dreams, the American Dream itself\n" +
            "• The Valley of Ashes – moral decay beneath the glittering wealth of the 1920s\n" +
            "• The eyes of Doctor T.J. Eckleburg – the eyes of God watching over a morally bankrupt society",
            "Mar 26, 2024"));
        notes.add(new Note("Acid-Base Titration",
            "Chemistry", "Lab",
            "Titration is used to determine the concentration of an unknown solution.\n\n" +
            "At the equivalence point, moles of acid = moles of base.\n" +
            "Formula: C₁V₁ = C₂V₂\n\n" +
            "Use a pH indicator (e.g. phenolphthalein) that changes colour near the equivalence point.",
            "Mar 25, 2024"));
        notes.add(new Note("Newton's Laws Summary",
            "Physics", "Mechanics",
            "1st Law: An object at rest stays at rest; an object in motion stays in motion unless acted upon by a net force.\n\n" +
            "2nd Law: F = ma. Force equals mass times acceleration.\n\n" +
            "3rd Law: For every action there is an equal and opposite reaction.",
            "Mar 23, 2024"));
        notes.add(new Note("WWI Causes – MAIN",
            "World History", "Review",
            "Militarism – European powers built up massive armies and navies.\n" +
            "Alliances – Complex treaty systems dragged nations into conflict.\n" +
            "Imperialism – Competition for colonies created tensions.\n" +
            "Nationalism – Pride and rivalry, especially in the Balkans.",
            "Mar 20, 2024"));
    }

    public List<Assignment>   getAssignments()   { return assignments; }
    public List<Attendance>   getAttendance()    { return attendance; }
    public List<Announcement> getAnnouncements() { return announcements; }
    public List<ClassSession> getSessions()      { return sessions; }
    public List<Grade>        getGrades()        { return grades; }
    public List<Note>         getNotes()         { return notes; }

    public double getGPA() {
        if (grades.isEmpty()) return 0;
        return grades.stream().mapToDouble(Grade::getPercentage).average().orElse(0) / 25.0;
    }
}
