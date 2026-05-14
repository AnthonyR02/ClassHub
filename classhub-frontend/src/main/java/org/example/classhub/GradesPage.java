package org.example.classhub;

import com.fasterxml.jackson.databind.JsonNode;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.*;

public class GradesPage {

    // ── colours ───────────────────────────────────────────────────────────────
    private static final String BG      = "#0f1117";
    private static final String SURFACE = "#181c27";
    private static final String BORDER  = "#ffffff12";
    private static final String TEXT    = "#e8eaf2";
    private static final String TEXT2   = "#9097b4";
    private static final String TEXT3   = "#5e6482";
    private static final String ACCENT  = "#6c8ef5";
    private static final String ROSE    = "#f5697b";
    private static final String GREEN   = "#3ecfb0";
    private static final String AMBER   = "#f5a623";

    // ── data model ────────────────────────────────────────────────────────────
    // Semester: { term, year }  e.g. { "Fall", "2024" }
    // Course: { courseName, scoreStr, letterGrade, credits, semesterKey }
    //   semesterKey = term + " " + year, e.g. "Fall 2024"

    private static class Semester {
        String term;   // Fall / Spring / Winter / Summer
        String year;
        List<String[]> courses = new ArrayList<>(); // {name, score, letter, credits}

        Semester(String term, String year) { this.term = term; this.year = year; }
        String key() { return term + " " + year; }
    }

    private List<Semester> semesters = new ArrayList<>();

    // What-if rows: { courseName, credits, currentLetter, whatIfLetter }
    private List<String[]> whatIfRows = new ArrayList<>();

    private Label currentGpaValue;
    private Label whatIfGpaValue;
    private Label totalCreditsValue;
    private VBox  classSection;
    private VBox  whatIfSection;
    private Scene scene;

    private double loadedGpa = 0.0;

    // ── constructor ───────────────────────────────────────────────────────────
    public GradesPage(Stage stage) {
        HBox root = new HBox(0);
        root.setStyle("-fx-background-color:" + BG + ";");
        root.getChildren().addAll(buildSidebar(stage), buildMain());
        scene = new Scene(root, 900, 600);
        loadGrades();
    }

    // ── load from backend ─────────────────────────────────────────────────────
    private void loadGrades() {
        if (SessionManager.isDevMode()) return;
        Thread thread = new Thread(() -> {
            try {
                FirebaseAuthClient client = new FirebaseAuthClient();
                String uid   = SessionManager.getUserId();
                String token = SessionManager.getIdToken();

                JsonNode summary     = client.getGpaSummary(uid, token);
                double  gpa          = summary.path("gpa").asDouble(0.0);
                int     totalCredits = summary.path("totalCredits").asInt(0);

                JsonNode coursesJson = client.getCourses(uid, token);
                Map<String, String[]> courseMap = new HashMap<>();
                for (JsonNode c : coursesJson) {
                    courseMap.put(c.path("id").asText(),
                        new String[]{ c.path("courseName").asText("?"),
                                      c.path("credits").asText("3") });
                }
                if (SessionManager.getCourses().isEmpty()) {
                    List<String[]> cl = new ArrayList<>();
                    for (JsonNode c : coursesJson)
                        cl.add(new String[]{ c.path("courseName").asText("?"),
                                             c.path("courseCode").asText(""),
                                             c.path("id").asText() });
                    SessionManager.setCourses(cl);
                }

                JsonNode records = client.getGradeRecords(uid, token);
                // Group by semester (use "semester" field if present, else default)
                Map<String, Semester> semMap = new LinkedHashMap<>();
                List<String[]> wiRows = new ArrayList<>();
                for (JsonNode r : records) {
                    String cid    = r.path("courseId").asText("");
                    String[] ci   = courseMap.getOrDefault(cid, new String[]{ cid, "3" });
                    String name   = ci[0];
                    String credits = ci[1];
                    String letter = r.path("letterGrade").asText("?");
                    String semKey = r.path("semester").asText("General");
                    int    score  = letterToScore(letter);

                    semMap.computeIfAbsent(semKey, k -> {
                        String[] parts = k.split(" ", 2);
                        return new Semester(parts[0], parts.length > 1 ? parts[1] : "");
                    }).courses.add(new String[]{ name, String.valueOf(score), letter, credits });
                    wiRows.add(new String[]{ name, credits, letter, letter });
                }
                semesters  = new ArrayList<>(semMap.values());
                whatIfRows = wiRows;
                loadedGpa  = gpa;

                Platform.runLater(() -> {
                    currentGpaValue.setText(String.format("%.2f", gpa));
                    whatIfGpaValue.setText(String.format("%.2f", gpa));
                    totalCreditsValue.setText(totalCredits + " cr");
                    refreshGradeSection();
                    refreshWhatIfSection();
                });

            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> currentGpaValue.setText("--"));
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    // ── sidebar ───────────────────────────────────────────────────────────────
    private VBox buildSidebar(Stage stage) {
        VBox sidebar = new VBox(0);
        sidebar.setMinWidth(200); sidebar.setMaxWidth(200);
        sidebar.setStyle("-fx-background-color:" + SURFACE + ";-fx-border-color:" + BORDER + ";-fx-border-width:0 1 0 0;");

        VBox logo = new VBox(3);
        logo.setPadding(new Insets(20, 16, 16, 16));
        logo.setStyle("-fx-border-color:" + BORDER + ";-fx-border-width:0 0 1 0;");
        Label logoTitle = new Label("ClassHub");
        logoTitle.setStyle("-fx-font-size:18px;-fx-font-weight:700;-fx-font-family:'Segoe UI';-fx-text-fill:" + TEXT + ";");
        Label logoSub = new Label("STUDENT PORTAL");
        logoSub.setStyle("-fx-font-size:9px;-fx-font-family:'Segoe UI';-fx-text-fill:" + TEXT3 + ";");
        logo.getChildren().addAll(logoTitle, logoSub);

        VBox nav = new VBox(2);
        nav.setPadding(new Insets(12, 8, 12, 8));
        VBox.setVgrow(nav, Priority.ALWAYS);
        nav.getChildren().add(navSection("MAIN"));
        Label dashItem = navItem("Dashboard", false);
        dashItem.setOnMouseClicked(e -> stage.setScene(ClassHubApplication.dashboardScene));
        Label calItem = navItem("Smart Calendar", false);
        calItem.setOnMouseClicked(e -> stage.setScene(ClassHubApplication.calendarScene));
        Label assignItem = navItem("Assignments", false);
        assignItem.setOnMouseClicked(e -> stage.setScene(ClassHubApplication.assignmentsScene));
        Label notesItem = navItem("Notes", false);
        notesItem.setOnMouseClicked(e -> stage.setScene(ClassHubApplication.notesScene));
        Label flashItemG = navItem("Flashcards", false);
        flashItemG.setOnMouseClicked(e -> stage.setScene(ClassHubApplication.flashcardsScene));
        nav.getChildren().addAll(dashItem, calItem, assignItem, navItem("Grades", true), notesItem, flashItemG);

        VBox footer = new VBox(8);
        footer.setPadding(new Insets(12));
        footer.setStyle("-fx-border-color:" + BORDER + ";-fx-border-width:1 0 0 0;");
        HBox userRow = new HBox(8);
        userRow.setAlignment(Pos.CENTER_LEFT);
        Label initials = new Label(SessionManager.getInitials());
        initials.setMinWidth(32); initials.setMinHeight(32);
        initials.setMaxWidth(32); initials.setMaxHeight(32);
        initials.setAlignment(Pos.CENTER);
        initials.setStyle("-fx-background-color:rgba(108,142,245,0.2);-fx-text-fill:" + ACCENT + ";-fx-font-size:11px;-fx-font-weight:700;-fx-font-family:'Segoe UI';-fx-background-radius:50;");
        VBox userInfo = new VBox(1);
        Label userName = new Label(SessionManager.getFullName());
        userName.setStyle("-fx-font-size:12px;-fx-font-weight:600;-fx-font-family:'Segoe UI';-fx-text-fill:" + TEXT + ";");
        Label userRole = new Label(SessionManager.getRole());
        userRole.setStyle("-fx-font-size:10px;-fx-font-family:'Segoe UI';-fx-text-fill:" + TEXT3 + ";");
        userInfo.getChildren().addAll(userName, userRole);
        userRow.getChildren().addAll(initials, userInfo);
        Button logoutBtn = new Button("Logout");
        logoutBtn.setMaxWidth(Double.MAX_VALUE);
        logoutBtn.setStyle("-fx-background-color:rgba(245,105,123,0.1);-fx-text-fill:" + ROSE + ";-fx-font-size:12px;-fx-font-family:'Segoe UI';-fx-background-radius:8;-fx-padding:7 12 7 12;-fx-cursor:hand;-fx-border-color:rgba(245,105,123,0.2);-fx-border-width:1;-fx-border-radius:8;");
        logoutBtn.setOnAction(e -> { SessionManager.logout(); stage.setScene(ClassHubApplication.loginScene); });
        footer.getChildren().addAll(userRow, logoutBtn);
        sidebar.getChildren().addAll(logo, nav, footer);
        return sidebar;
    }

    // ── main ──────────────────────────────────────────────────────────────────
    private VBox buildMain() {
        VBox main = new VBox(0);
        HBox.setHgrow(main, Priority.ALWAYS);

        HBox topbar = new HBox();
        topbar.setPrefHeight(52); topbar.setMinHeight(52);
        topbar.setAlignment(Pos.CENTER_LEFT);
        topbar.setPadding(new Insets(0, 20, 0, 20));
        topbar.setStyle("-fx-background-color:" + BG + ";-fx-border-color:" + BORDER + ";-fx-border-width:0 0 1 0;");
        Label title = new Label("Grades");
        title.setStyle("-fx-font-size:15px;-fx-font-weight:700;-fx-font-family:'Segoe UI';-fx-text-fill:" + TEXT + ";");
        topbar.getChildren().add(title);

        // Stat cards
        HBox cards = new HBox(12);
        VBox c1 = statCard("Current GPA", "...", ACCENT);
        currentGpaValue = (Label) c1.getChildren().get(1);
        VBox c2 = statCard("What-If GPA", "...", GREEN);
        whatIfGpaValue = (Label) c2.getChildren().get(1);
        VBox c3 = statCard("Total Credits", "0 cr", AMBER);
        totalCreditsValue = (Label) c3.getChildren().get(1);
        for (VBox c : new VBox[]{c1, c2, c3}) { HBox.setHgrow(c, Priority.ALWAYS); c.setMaxWidth(Double.MAX_VALUE); }
        cards.getChildren().addAll(c1, c2, c3);

        classSection = new VBox(8);
        classSection.setPadding(new Insets(16, 20, 16, 20));
        classSection.setStyle("-fx-background-color:" + SURFACE + ";-fx-border-color:" + BORDER + ";-fx-border-width:1;-fx-border-radius:12;-fx-background-radius:12;");
        refreshGradeSection();

        whatIfSection = new VBox(10);
        whatIfSection.setPadding(new Insets(16, 20, 16, 20));
        whatIfSection.setStyle("-fx-background-color:" + SURFACE + ";-fx-border-color:" + BORDER + ";-fx-border-width:1;-fx-border-radius:12;-fx-background-radius:12;");
        refreshWhatIfSection();

        VBox content = new VBox(16);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color:" + BG + ";");
        content.getChildren().addAll(cards, classSection, whatIfSection);

        ScrollPane scroll = new ScrollPane(content);
        scroll.setStyle("-fx-background-color:transparent;-fx-background:" + BG + ";");
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setBorder(Border.EMPTY);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        main.getChildren().addAll(topbar, scroll);
        return main;
    }

    // ── Grades section (semester-grouped) ─────────────────────────────────────
    private void refreshGradeSection() {
        classSection.getChildren().clear();

        // Title row with "+ Add Semester" and "+ Add Class" buttons
        Label title = new Label("Grades by Semester");
        title.setStyle("-fx-font-size:13px;-fx-font-weight:700;-fx-font-family:'Segoe UI';-fx-text-fill:#ffffff;");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button addClassBtn = new Button("+ Add Class");
        addClassBtn.setStyle("-fx-background-color:rgba(108,142,245,0.12);-fx-text-fill:" + ACCENT + ";-fx-font-size:11px;-fx-font-family:'Segoe UI';-fx-background-radius:6;-fx-padding:5 10 5 10;-fx-cursor:hand;-fx-border-color:rgba(108,142,245,0.2);-fx-border-width:1;-fx-border-radius:6;");
        addClassBtn.setOnAction(e -> showAddClassDialog());

        Button addSemBtn = new Button("+ Add Semester");
        addSemBtn.setStyle("-fx-background-color:rgba(62,207,176,0.1);-fx-text-fill:" + GREEN + ";-fx-font-size:11px;-fx-font-family:'Segoe UI';-fx-background-radius:6;-fx-padding:5 10 5 10;-fx-cursor:hand;-fx-border-color:rgba(62,207,176,0.2);-fx-border-width:1;-fx-border-radius:6;");
        addSemBtn.setOnAction(e -> showAddSemesterDialog());

        HBox titleRow = new HBox(8);
        titleRow.setAlignment(Pos.CENTER_LEFT);
        titleRow.getChildren().addAll(title, spacer, addClassBtn, addSemBtn);
        classSection.getChildren().add(titleRow);

        if (semesters.isEmpty()) {
            Label empty = new Label("No semesters yet. Click + Add Semester to get started.");
            empty.setStyle("-fx-font-size:12px;-fx-font-family:'Segoe UI';-fx-text-fill:" + TEXT3 + ";-fx-padding:8 0 0 0;");
            classSection.getChildren().add(empty);
            return;
        }

        for (int si = 0; si < semesters.size(); si++) {
            Semester sem = semesters.get(si);
            classSection.getChildren().add(buildSemesterBlock(sem, si));
        }
    }

    private VBox buildSemesterBlock(Semester sem, int semIdx) {
        VBox block = new VBox(6);
        block.setPadding(new Insets(12, 14, 12, 14));
        block.setStyle("-fx-background-color:#12151f;-fx-border-color:" + BORDER + ";-fx-border-width:1;-fx-border-radius:10;-fx-background-radius:10;");

        // Semester header row
        HBox semHeader = new HBox(10);
        semHeader.setAlignment(Pos.CENTER_LEFT);

        String termColor = termColor(sem.term);
        Label termBadge = new Label(sem.term.toUpperCase());
        termBadge.setStyle("-fx-font-size:9px;-fx-font-weight:700;-fx-letter-spacing:1px;-fx-font-family:'Segoe UI';-fx-text-fill:" + termColor + ";-fx-background-color:" + termBg(sem.term) + ";-fx-background-radius:4;-fx-padding:3 8 3 8;");

        Label semLabel = new Label(sem.term + " " + sem.year);
        semLabel.setStyle("-fx-font-size:13px;-fx-font-weight:700;-fx-font-family:'Segoe UI';-fx-text-fill:#ffffff;");

        // Semester GPA
        double semGpa = calcSemGpa(sem);
        Label semGpaLbl = new Label(sem.courses.isEmpty() ? "" : String.format("GPA: %.2f", semGpa));
        semGpaLbl.setStyle("-fx-font-size:11px;-fx-font-family:'Segoe UI';-fx-text-fill:" + ACCENT + ";");

        int semCr = sem.courses.stream().mapToInt(c -> parseCr(c[3])).sum();
        Label semCrLbl = new Label(sem.courses.isEmpty() ? "" : semCr + " cr");
        semCrLbl.setStyle("-fx-font-size:11px;-fx-font-family:'Segoe UI';-fx-text-fill:" + TEXT3 + ";");

        Region sp = new Region(); HBox.setHgrow(sp, Priority.ALWAYS);

        Button addCourseBtn = new Button("+ Class");
        addCourseBtn.setStyle("-fx-background-color:rgba(108,142,245,0.1);-fx-text-fill:" + ACCENT + ";-fx-font-size:10px;-fx-font-family:'Segoe UI';-fx-background-radius:5;-fx-padding:4 9 4 9;-fx-cursor:hand;");
        addCourseBtn.setOnAction(e -> showAddClassDialog(sem));

        Button deleteBtn = new Button("✕");
        deleteBtn.setStyle("-fx-background-color:transparent;-fx-text-fill:" + ROSE + ";-fx-font-size:11px;-fx-cursor:hand;-fx-padding:0 4 0 4;");
        deleteBtn.setOnAction(e -> {
            semesters.remove(semIdx);
            refreshGradeSection();
            syncWhatIfFromSemesters();
        });

        semHeader.getChildren().addAll(termBadge, semLabel, semGpaLbl, semCrLbl, sp, addCourseBtn, deleteBtn);
        block.getChildren().add(semHeader);

        if (sem.courses.isEmpty()) {
            Label noCourses = new Label("No classes added yet. Click + Class to add one.");
            noCourses.setStyle("-fx-font-size:11px;-fx-font-family:'Segoe UI';-fx-text-fill:" + TEXT3 + ";-fx-padding:4 0 0 0;");
            block.getChildren().add(noCourses);
            return block;
        }

        // Course rows
        for (int ci = 0; ci < sem.courses.size(); ci++) {
            String[] cls = sem.courses.get(ci);
            final int finalCi = ci;
            final Semester finalSem = sem;

            int    g       = Integer.parseInt(cls[1]);
            String color   = g >= 90 ? GREEN : g >= 80 ? AMBER : ROSE;
            String bgColor = g >= 90 ? "rgba(62,207,176,0.12)" : g >= 80 ? "rgba(245,166,35,0.12)" : "rgba(245,105,123,0.12)";

            HBox row = new HBox(12);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(10, 14, 10, 14));
            row.setStyle("-fx-background-color:" + BG + ";-fx-border-color:" + BORDER + ";-fx-border-width:1;-fx-border-radius:8;-fx-background-radius:8;");

            Label nameLbl = new Label(cls[0]);
            nameLbl.setStyle("-fx-font-size:13px;-fx-font-family:'Segoe UI';-fx-text-fill:#ffffff;");
            HBox.setHgrow(nameLbl, Priority.ALWAYS);

            double pts = letterToGpaPoints(cls[2]);
            Label ptsLbl = new Label(String.format("%.1f pts", pts));
            ptsLbl.setStyle("-fx-font-size:11px;-fx-font-family:'Segoe UI';-fx-text-fill:#ffffff;");

            Label letterLbl = new Label(cls[2]);
            letterLbl.setStyle("-fx-font-size:13px;-fx-font-weight:700;-fx-font-family:'Segoe UI';-fx-text-fill:#ffffff;-fx-background-color:" + bgColor + ";-fx-background-radius:6;-fx-padding:3 12 3 12;");

            Label scoreLbl = new Label(cls[1] + "%");
            scoreLbl.setStyle("-fx-font-size:13px;-fx-font-weight:600;-fx-font-family:'Segoe UI';-fx-text-fill:" + color + ";");

            Label credLbl = new Label(cls[3] + " cr");
            credLbl.setStyle("-fx-font-size:11px;-fx-font-family:'Segoe UI';-fx-text-fill:#ffffff;-fx-background-color:#1f2436;-fx-background-radius:4;-fx-padding:2 8 2 8;");

            Button delCourse = new Button("✕");
            delCourse.setStyle("-fx-background-color:transparent;-fx-text-fill:" + ROSE + ";-fx-font-size:10px;-fx-cursor:hand;-fx-padding:0 4 0 4;");
            delCourse.setOnAction(e -> {
                finalSem.courses.remove(finalCi);
                refreshGradeSection();
                syncWhatIfFromSemesters();
            });

            row.getChildren().addAll(nameLbl, credLbl, ptsLbl, letterLbl, scoreLbl, delCourse);
            block.getChildren().add(row);
        }
        return block;
    }

    // Sync whatIfRows to match all courses across all semesters
    private void syncWhatIfFromSemesters() {
        whatIfRows.clear();
        for (Semester sem : semesters)
            for (String[] c : sem.courses)
                whatIfRows.add(new String[]{ c[0], c[3], c[2], c[2] });
        refreshWhatIfSection();
        recalcWhatIfGpa();
    }

    private double calcSemGpa(Semester sem) {
        double qp = 0; int cr = 0;
        for (String[] c : sem.courses) {
            int credits = parseCr(c[3]);
            qp += letterToGpaPoints(c[2]) * credits;
            cr += credits;
        }
        return cr == 0 ? 0 : qp / cr;
    }

    // ── What-if calculator ────────────────────────────────────────────────────
    private void refreshWhatIfSection() {
        whatIfSection.getChildren().clear();

        Label title = new Label("What-If GPA Calculator");
        title.setStyle("-fx-font-size:13px;-fx-font-weight:700;-fx-font-family:'Segoe UI';-fx-text-fill:#ffffff;");
        Label subtitle = new Label("Change any grade below to see how it affects your GPA in real time.");
        subtitle.setStyle("-fx-font-size:11px;-fx-font-family:'Segoe UI';-fx-text-fill:" + TEXT3 + ";");
        subtitle.setWrapText(true);

        Button addHypBtn = new Button("+ Add Hypothetical Course");
        addHypBtn.setStyle("-fx-background-color:rgba(62,207,176,0.1);-fx-text-fill:" + GREEN + ";-fx-font-size:11px;-fx-font-family:'Segoe UI';-fx-background-radius:6;-fx-padding:5 10 5 10;-fx-cursor:hand;-fx-border-color:rgba(62,207,176,0.2);-fx-border-width:1;-fx-border-radius:6;");
        addHypBtn.setOnAction(e -> addHypotheticalRow());

        Button resetBtn = new Button("Reset to Actual");
        resetBtn.setStyle("-fx-background-color:#1f2436;-fx-text-fill:" + TEXT2 + ";-fx-font-size:11px;-fx-font-family:'Segoe UI';-fx-background-radius:6;-fx-padding:5 10 5 10;-fx-cursor:hand;-fx-border-color:#ffffff20;-fx-border-width:1;-fx-border-radius:6;");
        resetBtn.setOnAction(e -> { for (String[] r : whatIfRows) r[3] = r[2]; refreshWhatIfSection(); });

        HBox btnRow = new HBox(8, addHypBtn, resetBtn);
        btnRow.setAlignment(Pos.CENTER_LEFT);
        whatIfSection.getChildren().addAll(title, subtitle, btnRow);

        HBox header = new HBox(12);
        header.setPadding(new Insets(4, 16, 4, 16));
        Label h1 = headerLabel("Course"); HBox.setHgrow(h1, Priority.ALWAYS);
        Label h2 = headerLabel("Credits"); h2.setMinWidth(55);
        Label h3 = headerLabel("Actual");  h3.setMinWidth(70);
        Label h4 = headerLabel("What-If"); h4.setMinWidth(110);
        Label h5 = headerLabel("Impact");  h5.setMinWidth(70);
        header.getChildren().addAll(h1, h2, h3, h4, h5);
        whatIfSection.getChildren().add(header);

        for (int i = 0; i < whatIfRows.size(); i++)
            whatIfSection.getChildren().add(buildWhatIfRow(i));

        whatIfSection.getChildren().add(buildWhatIfSummary());
    }

    private HBox buildWhatIfRow(int idx) {
        String[] r = whatIfRows.get(idx);
        boolean isHypothetical = r[2].equals("?");

        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10, 16, 10, 16));
        row.setStyle("-fx-background-color:" + BG + ";-fx-border-color:" + BORDER + ";-fx-border-width:1;-fx-border-radius:8;-fx-background-radius:8;");

        Label nameLbl = new Label(r[0]);
        nameLbl.setStyle("-fx-font-size:13px;-fx-font-family:'Segoe UI';-fx-text-fill:" + (isHypothetical ? GREEN : TEXT) + ";" + (isHypothetical ? "-fx-font-style:italic;" : ""));
        HBox.setHgrow(nameLbl, Priority.ALWAYS);

        Label credLbl = new Label(r[1] + " cr");
        credLbl.setMinWidth(55);
        credLbl.setStyle("-fx-font-size:12px;-fx-font-family:'Segoe UI';-fx-text-fill:#ffffff;");

        Label actualLbl = new Label(isHypothetical ? "NEW" : r[2]);
        actualLbl.setMinWidth(70);
        actualLbl.setStyle("-fx-font-size:12px;-fx-font-weight:700;-fx-font-family:'Segoe UI';-fx-text-fill:" + (isHypothetical ? GREEN : gradeColor(r[2])) + ";");

        ComboBox<String> whatIfBox = new ComboBox<>();
        whatIfBox.getItems().addAll("A+","A","A-","B+","B","B-","C+","C","C-","D+","D","F");
        whatIfBox.setValue(r[3].equals("?") ? "A" : r[3]);
        whatIfBox.setMinWidth(110); whatIfBox.setMaxWidth(110);
        whatIfBox.setStyle("-fx-background-color:#1f2436;-fx-text-fill:" + TEXT + ";-fx-font-size:12px;-fx-font-family:'Segoe UI';");
        whatIfBox.setOnAction(e -> { r[3] = whatIfBox.getValue(); recalcWhatIfGpa(); refreshWhatIfSection(); });

        double actualPts  = isHypothetical ? 0 : letterToGpaPoints(r[2]);
        double whatIfPts  = letterToGpaPoints(r[3].equals("?") ? "A" : r[3]);
        double diff       = whatIfPts - actualPts;
        String impactText = isHypothetical ? "+" + String.format("%.1f", whatIfPts)
                          : (diff > 0 ? "▲ +" : diff < 0 ? "▼ " : "= ") + String.format("%.1f", diff);
        Label impactLbl = new Label(impactText);
        impactLbl.setMinWidth(70);
        impactLbl.setStyle("-fx-font-size:12px;-fx-font-weight:700;-fx-font-family:'Segoe UI';-fx-text-fill:" + (diff > 0 ? GREEN : diff < 0 ? ROSE : TEXT3) + ";");

        if (isHypothetical) {
            Button removeBtn = new Button("✕");
            removeBtn.setStyle("-fx-background-color:transparent;-fx-text-fill:" + ROSE + ";-fx-font-size:11px;-fx-cursor:hand;-fx-padding:0 4 0 4;");
            removeBtn.setOnAction(e -> { whatIfRows.remove(idx); refreshWhatIfSection(); });
            row.getChildren().addAll(nameLbl, credLbl, actualLbl, whatIfBox, impactLbl, removeBtn);
        } else {
            row.getChildren().addAll(nameLbl, credLbl, actualLbl, whatIfBox, impactLbl);
        }
        return row;
    }

    private VBox buildWhatIfSummary() {
        double totalQP = 0; int totalCr = 0;
        for (String[] r : whatIfRows) {
            int cr = parseCr(r[1]);
            totalQP += letterToGpaPoints(r[3].equals("?") ? "A" : r[3]) * cr;
            totalCr += cr;
        }
        double whatIfGpa = totalCr == 0 ? 0 : totalQP / totalCr;
        double diff      = whatIfGpa - loadedGpa;
        String diffText  = (diff >= 0 ? "+ " : "- ") + String.format("%.2f", Math.abs(diff));
        String diffColor = diff > 0 ? GREEN : diff < 0 ? ROSE : TEXT3;

        VBox box = new VBox(6);
        box.setPadding(new Insets(14, 16, 14, 16));
        box.setStyle("-fx-background-color:rgba(108,142,245,0.06);-fx-border-color:rgba(108,142,245,0.15);-fx-border-width:1;-fx-border-radius:8;-fx-background-radius:8;");

        HBox summaryRow = new HBox(16);
        summaryRow.setAlignment(Pos.CENTER_LEFT);

        VBox cur = new VBox(2);
        cur.getChildren().addAll(styledSmallLabel("Current GPA"), styledBigLabel(String.format("%.2f", loadedGpa), ACCENT));
        Label arrow = new Label("→");
        arrow.setStyle("-fx-font-size:20px;-fx-text-fill:" + TEXT3 + ";");
        VBox proj = new VBox(2);
        proj.getChildren().addAll(styledSmallLabel("Projected GPA"), styledBigLabel(String.format("%.2f", whatIfGpa), GREEN));
        VBox change = new VBox(2);
        change.getChildren().addAll(styledSmallLabel("Change"), styledBigLabel(diffText, diffColor));

        summaryRow.getChildren().addAll(cur, arrow, proj, new Region() {{ HBox.setHgrow(this, Priority.ALWAYS); }}, change);
        box.getChildren().add(summaryRow);
        Label interp = new Label("Projected standing: " + gpaToLetter(whatIfGpa));
        interp.setStyle("-fx-font-size:11px;-fx-font-family:'Segoe UI';-fx-text-fill:" + TEXT3 + ";");
        box.getChildren().add(interp);
        return box;
    }

    private void addHypotheticalRow() {
        whatIfRows.add(new String[]{ "Hypothetical Course", "3", "?", "A" });
        refreshWhatIfSection();
        recalcWhatIfGpa();
    }

    private void recalcWhatIfGpa() {
        double totalQP = 0; int totalCr = 0;
        for (String[] r : whatIfRows) {
            int cr = parseCr(r[1]);
            totalQP += letterToGpaPoints(r[3].equals("?") ? "A" : r[3]) * cr;
            totalCr += cr;
        }
        double gpa = totalCr == 0 ? 0 : totalQP / totalCr;
        if (whatIfGpaValue != null)
            Platform.runLater(() -> whatIfGpaValue.setText(String.format("%.2f", gpa)));
    }

    // ── Add Semester dialog ───────────────────────────────────────────────────
    private void showAddSemesterDialog() {
        Stage dialog = new Stage();
        dialog.setTitle("Add Semester");
        dialog.initModality(Modality.APPLICATION_MODAL);

        VBox form = new VBox(14);
        form.setPadding(new Insets(28));
        form.setStyle("-fx-background-color:" + SURFACE + ";");
        form.setPrefWidth(340);

        Label heading = new Label("Add Semester");
        heading.setStyle("-fx-font-size:16px;-fx-font-weight:700;-fx-font-family:'Segoe UI';-fx-text-fill:#ffffff;");

        Label termLbl = new Label("Term");
        termLbl.setStyle("-fx-font-size:11px;-fx-font-family:'Segoe UI';-fx-text-fill:#ffffff;");

        // Term buttons row
        String[] terms = {"Fall", "Spring", "Winter", "Summer"};
        String[] termColors = {AMBER, GREEN, ACCENT, ROSE};
        HBox termRow = new HBox(8);
        termRow.setAlignment(Pos.CENTER_LEFT);
        final String[] selectedTerm = {"Fall"};

        ToggleGroup tg = new ToggleGroup();
        for (int i = 0; i < terms.length; i++) {
            String t = terms[i];
            String tc = termColors[i];
            ToggleButton tb = new ToggleButton(t);
            tb.setToggleGroup(tg);
            tb.setSelected(t.equals("Fall"));
            tb.setStyle(t.equals("Fall")
                ? "-fx-background-color:" + tc + ";-fx-text-fill:white;-fx-font-size:12px;-fx-font-weight:600;-fx-font-family:'Segoe UI';-fx-background-radius:7;-fx-padding:7 16 7 16;-fx-cursor:hand;"
                : "-fx-background-color:#1a1f2e;-fx-text-fill:#ffffff;-fx-font-size:12px;-fx-font-family:'Segoe UI';-fx-background-radius:7;-fx-padding:7 16 7 16;-fx-cursor:hand;-fx-border-color:" + BORDER + ";-fx-border-width:1;-fx-border-radius:7;");
            tb.selectedProperty().addListener((obs, was, is) -> {
                if (is) {
                    selectedTerm[0] = t;
                    tb.setStyle("-fx-background-color:" + tc + ";-fx-text-fill:white;-fx-font-size:12px;-fx-font-weight:600;-fx-font-family:'Segoe UI';-fx-background-radius:7;-fx-padding:7 16 7 16;-fx-cursor:hand;");
                } else {
                    tb.setStyle("-fx-background-color:#1a1f2e;-fx-text-fill:#ffffff;-fx-font-size:12px;-fx-font-family:'Segoe UI';-fx-background-radius:7;-fx-padding:7 16 7 16;-fx-cursor:hand;-fx-border-color:" + BORDER + ";-fx-border-width:1;-fx-border-radius:7;");
                }
            });
            termRow.getChildren().add(tb);
        }

        Label yearLbl = new Label("Year");
        yearLbl.setStyle("-fx-font-size:11px;-fx-font-family:'Segoe UI';-fx-text-fill:#ffffff;");
        ComboBox<String> yearBox = styledComboBox();
        int currentYear = java.time.Year.now().getValue();
        for (int y = currentYear + 1; y >= currentYear - 6; y--)
            yearBox.getItems().add(String.valueOf(y));
        yearBox.setValue(String.valueOf(currentYear));

        Label errLbl = new Label("");
        errLbl.setStyle("-fx-text-fill:" + ROSE + ";-fx-font-size:11px;");

        Button createBtn = new Button("Add Semester");
        createBtn.setMaxWidth(Double.MAX_VALUE);
        createBtn.setStyle("-fx-background-color:" + ACCENT + ";-fx-text-fill:white;-fx-font-size:13px;-fx-font-weight:600;-fx-font-family:'Segoe UI';-fx-background-radius:8;-fx-padding:10 0 10 0;-fx-cursor:hand;");
        createBtn.setOnAction(e -> {
            String term = selectedTerm[0];
            String year = yearBox.getValue();
            boolean dup = semesters.stream().anyMatch(s -> s.term.equals(term) && s.year.equals(year));
            if (dup) { errLbl.setText(term + " " + year + " already exists."); return; }
            semesters.add(new Semester(term, year));
            refreshGradeSection();
            dialog.close();
        });

        form.getChildren().addAll(heading, termLbl, termRow, yearLbl, yearBox, errLbl, createBtn);
        dialog.setScene(new javafx.scene.Scene(form));
        dialog.show();
    }

    // ── Add Class dialog (no pre-selected semester) ───────────────────────────
    private void showAddClassDialog() {
        if (semesters.isEmpty()) {
            showAddSemesterDialog();
            return;
        }
        showAddClassDialog(semesters.get(semesters.size() - 1));
    }

    // ── Add Class dialog (with pre-selected semester) ─────────────────────────
    private void showAddClassDialog(Semester preSelected) {
        Stage dialog = new Stage();
        dialog.setTitle("Add Class");
        dialog.initModality(Modality.APPLICATION_MODAL);

        VBox form = new VBox(12);
        form.setPadding(new Insets(28));
        form.setStyle("-fx-background-color:" + SURFACE + ";");
        form.setPrefWidth(380);

        Label heading = new Label("Add Class");
        heading.setStyle("-fx-font-size:16px;-fx-font-weight:700;-fx-font-family:'Segoe UI';-fx-text-fill:#ffffff;");

        // Semester selector
        Label semLbl = new Label("Semester");
        semLbl.setStyle("-fx-font-size:11px;-fx-font-family:'Segoe UI';-fx-text-fill:#ffffff;");
        ComboBox<String> semBox = styledComboBox();
        for (Semester s : semesters) semBox.getItems().add(s.key());
        semBox.setValue(preSelected.key());

        // Course name
        Label courseLbl = new Label("Course Name");
        courseLbl.setStyle("-fx-font-size:11px;-fx-font-family:'Segoe UI';-fx-text-fill:#ffffff;");
        // Populate from SessionManager courses
        ComboBox<String> courseBox = styledComboBox();
        courseBox.setEditable(true);
        List<String[]> sessionCourses = SessionManager.getCourses();
        for (String[] c : sessionCourses) courseBox.getItems().add(c[0]);
        if (!courseBox.getItems().isEmpty()) courseBox.setValue(courseBox.getItems().get(0));
        else courseBox.setPromptText("Type course name...");

        // Letter grade
        Label gradeLbl = new Label("Letter Grade");
        gradeLbl.setStyle("-fx-font-size:11px;-fx-font-family:'Segoe UI';-fx-text-fill:#ffffff;");
        ComboBox<String> letterBox = styledComboBox();
        letterBox.getItems().addAll("A+","A","A-","B+","B","B-","C+","C","C-","D+","D","F");
        letterBox.setValue("A");

        // Credits
        Label credLbl = new Label("Credits");
        credLbl.setStyle("-fx-font-size:11px;-fx-font-family:'Segoe UI';-fx-text-fill:#ffffff;");
        ComboBox<String> credBox = styledComboBox();
        credBox.getItems().addAll("1","2","3","4","5","6");
        credBox.setValue("3");

        // GPA preview
        Label previewLbl = new Label("GPA points: 4.0");
        previewLbl.setStyle("-fx-font-size:11px;-fx-font-family:'Segoe UI';-fx-text-fill:" + GREEN + ";");
        letterBox.setOnAction(e -> previewLbl.setText(String.format("GPA points: %.1f", letterToGpaPoints(letterBox.getValue()))));

        Label errLbl = new Label("");
        errLbl.setStyle("-fx-text-fill:" + ROSE + ";-fx-font-size:11px;");

        Button saveBtn = new Button("Add Class");
        saveBtn.setMaxWidth(Double.MAX_VALUE);
        saveBtn.setStyle("-fx-background-color:" + ACCENT + ";-fx-text-fill:white;-fx-font-size:13px;-fx-font-weight:600;-fx-font-family:'Segoe UI';-fx-background-radius:8;-fx-padding:10 0 10 0;-fx-cursor:hand;");
        saveBtn.setOnAction(e -> {
            String semKey  = semBox.getValue();
            String course  = courseBox.getEditor().getText().trim();
            String letter  = letterBox.getValue();
            String credits = credBox.getValue();
            if (semKey == null || course.isEmpty() || letter == null) {
                errLbl.setText("Please fill in all fields."); return;
            }
            Semester target = semesters.stream().filter(s -> s.key().equals(semKey)).findFirst().orElse(null);
            if (target == null) { errLbl.setText("Semester not found."); return; }
            int score = letterToScore(letter);
            target.courses.add(new String[]{ course, String.valueOf(score), letter, credits });
            syncWhatIfFromSemesters();
            refreshGradeSection();
            // Also update total credits stat
            int totalCr = semesters.stream().flatMap(s -> s.courses.stream()).mapToInt(c -> parseCr(c[3])).sum();
            if (totalCreditsValue != null) totalCreditsValue.setText(totalCr + " cr");
            // Recalc overall GPA
            double totalQP = 0; int totalCrCalc = 0;
            for (Semester s : semesters)
                for (String[] c : s.courses) {
                    int cr = parseCr(c[3]);
                    totalQP += letterToGpaPoints(c[2]) * cr;
                    totalCrCalc += cr;
                }
            double newGpa = totalCrCalc == 0 ? 0 : totalQP / totalCrCalc;
            loadedGpa = newGpa;
            if (currentGpaValue != null) currentGpaValue.setText(String.format("%.2f", newGpa));
            if (whatIfGpaValue != null)  whatIfGpaValue.setText(String.format("%.2f", newGpa));
            dialog.close();
        });

        form.getChildren().addAll(heading, semLbl, semBox, courseLbl, courseBox,
                gradeLbl, letterBox, credLbl, credBox, previewLbl, errLbl, saveBtn);
        dialog.setScene(new javafx.scene.Scene(form));
        dialog.show();
    }

    // ── helpers ───────────────────────────────────────────────────────────────
    private String termColor(String term) {
        return switch (term) {
            case "Fall"   -> AMBER;
            case "Spring" -> GREEN;
            case "Winter" -> ACCENT;
            case "Summer" -> ROSE;
            default       -> TEXT3;
        };
    }
    private String termBg(String term) {
        return switch (term) {
            case "Fall"   -> "rgba(245,166,35,0.12)";
            case "Spring" -> "rgba(62,207,176,0.12)";
            case "Winter" -> "rgba(108,142,245,0.12)";
            case "Summer" -> "rgba(245,105,123,0.12)";
            default       -> "rgba(255,255,255,0.06)";
        };
    }

    private Label styledSmallLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-size:10px;-fx-font-family:'Segoe UI';-fx-text-fill:" + TEXT3 + ";");
        return l;
    }
    private Label styledBigLabel(String text, String color) {
        Label l = new Label(text);
        l.setStyle("-fx-font-size:20px;-fx-font-weight:700;-fx-font-family:'Segoe UI';-fx-text-fill:" + color + ";");
        return l;
    }

    private ComboBox<String> styledComboBox() {
        ComboBox<String> cb = new ComboBox<>();
        cb.setMaxWidth(Double.MAX_VALUE);
        cb.setStyle("-fx-background-color:#1f2436;-fx-text-fill:#ffffff;-fx-font-size:13px;-fx-font-family:'Segoe UI';");
        return cb;
    }

    private Label headerLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-size:10px;-fx-font-weight:700;-fx-font-family:'Segoe UI';-fx-text-fill:#ffffff;");
        return l;
    }

    private VBox statCard(String label, String value, String color) {
        VBox card = new VBox(6);
        card.setPadding(new Insets(14, 16, 14, 16));
        card.setStyle("-fx-background-color:" + SURFACE + ";-fx-border-color:" + BORDER + ";-fx-border-width:1;-fx-border-radius:12;-fx-background-radius:12;");
        Label lbl = new Label(label);
        lbl.setStyle("-fx-font-size:11px;-fx-font-family:'Segoe UI';-fx-text-fill:" + TEXT3 + ";");
        Label val = new Label(value);
        val.setStyle("-fx-font-size:26px;-fx-font-weight:700;-fx-font-family:'Segoe UI';-fx-text-fill:" + color + ";");
        card.getChildren().addAll(lbl, val);
        return card;
    }

    private Label navSection(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-size:9px;-fx-font-family:'Segoe UI';-fx-text-fill:" + TEXT3 + ";-fx-padding:10 10 6 10;");
        return l;
    }

    private Label navItem(String text, boolean active) {
        Label l = new Label(text);
        l.setMaxWidth(Double.MAX_VALUE);
        l.setPadding(new Insets(9, 12, 9, 12));
        l.setStyle(active
            ? "-fx-background-color:rgba(108,142,245,0.12);-fx-text-fill:" + ACCENT + ";-fx-font-size:13px;-fx-font-family:'Segoe UI';-fx-font-weight:600;-fx-background-radius:8;-fx-border-color:rgba(108,142,245,0.2);-fx-border-width:1;-fx-border-radius:8;"
            : "-fx-background-color:transparent;-fx-text-fill:" + TEXT2 + ";-fx-font-size:13px;-fx-font-family:'Segoe UI';-fx-background-radius:8;-fx-border-color:transparent;-fx-border-width:1;-fx-border-radius:8;"
        );
        return l;
    }

    private String gradeColor(String letter) {
        int s = letterToScore(letter);
        return s >= 90 ? GREEN : s >= 80 ? AMBER : ROSE;
    }

    private static int letterToScore(String letter) {
        if (letter == null) return 75;
        return switch (letter.toUpperCase().trim()) {
            case "A+","A"  -> 97;
            case "A-"      -> 92;
            case "B+","B"  -> 87;
            case "B-"      -> 82;
            case "C+","C"  -> 77;
            case "C-"      -> 72;
            case "D+","D"  -> 65;
            default        -> 55;
        };
    }

    private static double letterToGpaPoints(String letter) {
        if (letter == null) return 0;
        return switch (letter.toUpperCase().trim()) {
            case "A+"      -> 4.0;
            case "A"       -> 4.0;
            case "A-"      -> 3.7;
            case "B+"      -> 3.3;
            case "B"       -> 3.0;
            case "B-"      -> 2.7;
            case "C+"      -> 2.3;
            case "C"       -> 2.0;
            case "C-"      -> 1.7;
            case "D+"      -> 1.3;
            case "D"       -> 1.0;
            default        -> 0.0;
        };
    }

    private static String gpaToLetter(double gpa) {
        if (gpa >= 3.7) return "A (Dean's List)";
        if (gpa >= 3.3) return "A-/B+ (Good Standing)";
        if (gpa >= 3.0) return "B (Good Standing)";
        if (gpa >= 2.7) return "B- (Satisfactory)";
        if (gpa >= 2.0) return "C (Satisfactory)";
        return "Below Satisfactory";
    }

    private static int parseCr(String s) {
        try { return Integer.parseInt(s.trim()); } catch (Exception e) { return 3; }
    }

    public Scene getScene() { return scene; }
}
