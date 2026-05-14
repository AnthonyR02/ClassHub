package org.example.classhub;

import com.fasterxml.jackson.databind.JsonNode;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    // ── state ─────────────────────────────────────────────────────────────────
    // CLASSES: { courseName, scoreStr, letterGrade, credits }
    private List<String[]> gradeRows = new ArrayList<>();

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

                // GPA summary
                JsonNode summary     = client.getGpaSummary(uid, token);
                double  gpa          = summary.path("gpa").asDouble(0.0);
                int     totalCredits = summary.path("totalCredits").asInt(0);

                // Courses map: id -> { name, credits }
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

                // Grade records
                JsonNode records = client.getGradeRecords(uid, token);
                List<String[]> rows = new ArrayList<>();
                List<String[]> wiRows = new ArrayList<>();
                for (JsonNode r : records) {
                    String cid    = r.path("courseId").asText("");
                    String[] ci   = courseMap.getOrDefault(cid, new String[]{ cid, "3" });
                    String name   = ci[0];
                    String credits = ci[1];
                    String letter = r.path("letterGrade").asText("?");
                    int    score  = letterToScore(letter);
                    rows.add(new String[]{ name, String.valueOf(score), letter, credits });
                    wiRows.add(new String[]{ name, credits, letter, letter }); // last = what-if copy
                }
                gradeRows  = rows;
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
        nav.getChildren().addAll(dashItem, calItem, assignItem, navItem("Grades", true), notesItem);

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

        // ── Stat cards ────────────────────────────────────────────────────────
        HBox cards = new HBox(12);

        VBox c1 = statCard("Current GPA", "...", ACCENT);
        currentGpaValue = (Label) c1.getChildren().get(1);

        VBox c2 = statCard("What-If GPA", "...", GREEN);
        whatIfGpaValue = (Label) c2.getChildren().get(1);

        VBox c3 = statCard("Total Credits", "0 cr", AMBER);
        totalCreditsValue = (Label) c3.getChildren().get(1);

        for (VBox c : new VBox[]{c1, c2, c3}) {
            HBox.setHgrow(c, Priority.ALWAYS);
            c.setMaxWidth(Double.MAX_VALUE);
        }
        cards.getChildren().addAll(c1, c2, c3);

        // ── Grade section placeholder ─────────────────────────────────────────
        classSection = new VBox(8);
        classSection.setPadding(new Insets(16, 20, 16, 20));
        classSection.setStyle("-fx-background-color:" + SURFACE + ";-fx-border-color:" + BORDER + ";-fx-border-width:1;-fx-border-radius:12;-fx-background-radius:12;");
        refreshGradeSection();

        // ── What-if section placeholder ───────────────────────────────────────
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

    // ── Grades by class section ───────────────────────────────────────────────
    private void refreshGradeSection() {
        classSection.getChildren().clear();

        Label title = new Label("Grades by Class");
        title.setStyle("-fx-font-size:13px;-fx-font-weight:700;-fx-font-family:'Segoe UI';-fx-text-fill:" + TEXT + ";");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        Button addBtn = new Button("+ Add Grade");
        addBtn.setStyle("-fx-background-color:rgba(108,142,245,0.12);-fx-text-fill:" + ACCENT + ";-fx-font-size:11px;-fx-font-family:'Segoe UI';-fx-background-radius:6;-fx-padding:5 10 5 10;-fx-cursor:hand;-fx-border-color:rgba(108,142,245,0.2);-fx-border-width:1;-fx-border-radius:6;");
        addBtn.setOnAction(e -> showAddGradeDialog());
        HBox titleRow = new HBox();
        titleRow.setAlignment(Pos.CENTER_LEFT);
        titleRow.getChildren().addAll(title, spacer, addBtn);
        classSection.getChildren().add(titleRow);

        if (gradeRows.isEmpty()) {
            Label empty = new Label("No grades yet. Click + Add Grade to get started.");
            empty.setStyle("-fx-font-size:12px;-fx-font-family:'Segoe UI';-fx-text-fill:" + TEXT3 + ";-fx-padding:8 0 0 0;");
            classSection.getChildren().add(empty);
            return;
        }

        for (String[] cls : gradeRows) {
            int    g       = Integer.parseInt(cls[1]);
            String color   = g >= 90 ? GREEN : g >= 80 ? AMBER : ROSE;
            String bgColor = g >= 90 ? "rgba(62,207,176,0.12)" : g >= 80 ? "rgba(245,166,35,0.12)" : "rgba(245,105,123,0.12)";

            HBox row = new HBox(12);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(12, 16, 12, 16));
            row.setStyle("-fx-background-color:" + BG + ";-fx-border-color:" + BORDER + ";-fx-border-width:1;-fx-border-radius:8;-fx-background-radius:8;");

            Label nameLbl = new Label(cls[0]);
            nameLbl.setStyle("-fx-font-size:13px;-fx-font-family:'Segoe UI';-fx-text-fill:" + TEXT + ";");
            HBox.setHgrow(nameLbl, Priority.ALWAYS);

            // GPA points label
            double pts = letterToGpaPoints(cls[2]);
            Label ptsLbl = new Label(String.format("%.1f pts", pts));
            ptsLbl.setStyle("-fx-font-size:11px;-fx-font-family:'Segoe UI';-fx-text-fill:" + TEXT3 + ";");

            Label letterLbl = new Label(cls[2]);
            letterLbl.setStyle("-fx-font-size:13px;-fx-font-weight:700;-fx-font-family:'Segoe UI';-fx-text-fill:" + color + ";-fx-background-color:" + bgColor + ";-fx-background-radius:6;-fx-padding:3 12 3 12;");

            Label scoreLbl = new Label(cls[1] + "%");
            scoreLbl.setStyle("-fx-font-size:13px;-fx-font-weight:600;-fx-font-family:'Segoe UI';-fx-text-fill:" + color + ";");

            // Credits badge
            Label credLbl = new Label(cls.length > 3 ? cls[3] + " cr" : "3 cr");
            credLbl.setStyle("-fx-font-size:11px;-fx-font-family:'Segoe UI';-fx-text-fill:" + TEXT3 + ";-fx-background-color:#1f2436;-fx-background-radius:4;-fx-padding:2 8 2 8;");

            row.getChildren().addAll(nameLbl, credLbl, ptsLbl, letterLbl, scoreLbl);
            classSection.getChildren().add(row);
        }
    }

    // ── What-if calculator ────────────────────────────────────────────────────
    private void refreshWhatIfSection() {
        whatIfSection.getChildren().clear();

        // Header
        Label title = new Label("What-If GPA Calculator");
        title.setStyle("-fx-font-size:13px;-fx-font-weight:700;-fx-font-family:'Segoe UI';-fx-text-fill:" + TEXT + ";");

        Label subtitle = new Label("Change any grade below to see how it affects your GPA in real time.");
        subtitle.setStyle("-fx-font-size:11px;-fx-font-family:'Segoe UI';-fx-text-fill:" + TEXT3 + ";");
        subtitle.setWrapText(true);

        // Add hypothetical course row
        Button addHypBtn = new Button("+ Add Hypothetical Course");
        addHypBtn.setStyle("-fx-background-color:rgba(62,207,176,0.1);-fx-text-fill:" + GREEN + ";-fx-font-size:11px;-fx-font-family:'Segoe UI';-fx-background-radius:6;-fx-padding:5 10 5 10;-fx-cursor:hand;-fx-border-color:rgba(62,207,176,0.2);-fx-border-width:1;-fx-border-radius:6;");
        addHypBtn.setOnAction(e -> addHypotheticalRow());

        // Reset button
        Button resetBtn = new Button("Reset to Actual");
        resetBtn.setStyle("-fx-background-color:#1f2436;-fx-text-fill:" + TEXT2 + ";-fx-font-size:11px;-fx-font-family:'Segoe UI';-fx-background-radius:6;-fx-padding:5 10 5 10;-fx-cursor:hand;-fx-border-color:#ffffff20;-fx-border-width:1;-fx-border-radius:6;");
        resetBtn.setOnAction(e -> {
            // reset all what-if grades back to actual
            for (String[] r : whatIfRows) r[3] = r[2];
            refreshWhatIfSection();
        });

        HBox btnRow = new HBox(8, addHypBtn, resetBtn);
        btnRow.setAlignment(Pos.CENTER_LEFT);

        whatIfSection.getChildren().addAll(title, subtitle, btnRow);

        // Column headers
        HBox header = new HBox(12);
        header.setPadding(new Insets(4, 16, 4, 16));
        Label h1 = headerLabel("Course");
        HBox.setHgrow(h1, Priority.ALWAYS);
        Label h2 = headerLabel("Credits");
        h2.setMinWidth(55);
        Label h3 = headerLabel("Actual");
        h3.setMinWidth(70);
        Label h4 = headerLabel("What-If");
        h4.setMinWidth(110);
        Label h5 = headerLabel("Impact");
        h5.setMinWidth(70);
        header.getChildren().addAll(h1, h2, h3, h4, h5);
        whatIfSection.getChildren().add(header);

        // One row per course in whatIfRows
        for (int i = 0; i < whatIfRows.size(); i++) {
            whatIfSection.getChildren().add(buildWhatIfRow(i));
        }

        // Summary box
        whatIfSection.getChildren().add(buildWhatIfSummary());
    }

    private HBox buildWhatIfRow(int idx) {
        String[] r = whatIfRows.get(idx);
        // r = { courseName, credits, actualLetter, whatIfLetter }
        boolean isHypothetical = r[2].equals("?");

        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10, 16, 10, 16));
        row.setStyle("-fx-background-color:" + BG + ";-fx-border-color:" + BORDER + ";-fx-border-width:1;-fx-border-radius:8;-fx-background-radius:8;");

        // Course name
        Label nameLbl = new Label(r[0]);
        nameLbl.setStyle("-fx-font-size:13px;-fx-font-family:'Segoe UI';-fx-text-fill:" + (isHypothetical ? GREEN : TEXT) + ";");
        if (isHypothetical) nameLbl.setStyle(nameLbl.getStyle() + "-fx-font-style:italic;");
        HBox.setHgrow(nameLbl, Priority.ALWAYS);

        // Credits
        Label credLbl = new Label(r[1] + " cr");
        credLbl.setMinWidth(55);
        credLbl.setStyle("-fx-font-size:12px;-fx-font-family:'Segoe UI';-fx-text-fill:" + TEXT3 + ";");

        // Actual grade badge
        Label actualLbl = new Label(isHypothetical ? "NEW" : r[2]);
        actualLbl.setMinWidth(70);
        String actualColor = isHypothetical ? GREEN : gradeColor(r[2]);
        actualLbl.setStyle("-fx-font-size:12px;-fx-font-weight:700;-fx-font-family:'Segoe UI';-fx-text-fill:" + actualColor + ";");

        // What-if dropdown
        ComboBox<String> whatIfBox = new ComboBox<>();
        whatIfBox.getItems().addAll("A+", "A", "A-", "B+", "B", "B-", "C+", "C", "C-", "D+", "D", "F");
        whatIfBox.setValue(r[3].equals("?") ? "A" : r[3]);
        whatIfBox.setMinWidth(110); whatIfBox.setMaxWidth(110);
        whatIfBox.setStyle("-fx-background-color:#1f2436;-fx-text-fill:" + TEXT + ";-fx-font-size:12px;-fx-font-family:'Segoe UI';");
        whatIfBox.setOnAction(e -> {
            r[3] = whatIfBox.getValue();
            recalcWhatIfGpa();
            // refresh just the impact label by re-rendering
            refreshWhatIfSection();
        });

        // Impact label — shows GPA change
        double actualPts  = isHypothetical ? 0 : letterToGpaPoints(r[2]);
        double whatIfPts  = letterToGpaPoints(r[3].equals("?") ? "A" : r[3]);
        double diff       = whatIfPts - actualPts;
        String impactText = isHypothetical ? "+" + String.format("%.1f", whatIfPts)
                          : (diff > 0 ? "▲ +" : diff < 0 ? "▼ " : "= ") + String.format("%.1f", diff);
        String impactColor = diff > 0 ? GREEN : diff < 0 ? ROSE : TEXT3;
        Label impactLbl = new Label(impactText);
        impactLbl.setMinWidth(70);
        impactLbl.setStyle("-fx-font-size:12px;-fx-font-weight:700;-fx-font-family:'Segoe UI';-fx-text-fill:" + impactColor + ";");

        // Remove button (hypothetical courses only)
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
        // Calculate current GPA from whatIfRows
        double totalQP = 0; int totalCr = 0;
        for (String[] r : whatIfRows) {
            int cr = parseCr(r[1]);
            double pts = letterToGpaPoints(r[3].equals("?") ? "A" : r[3]);
            totalQP += pts * cr;
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

        VBox current = new VBox(2);
        Label curLbl = new Label("Current GPA");
        curLbl.setStyle("-fx-font-size:10px;-fx-font-family:'Segoe UI';-fx-text-fill:" + TEXT3 + ";");
        Label curVal = new Label(String.format("%.2f", loadedGpa));
        curVal.setStyle("-fx-font-size:20px;-fx-font-weight:700;-fx-font-family:'Segoe UI';-fx-text-fill:" + ACCENT + ";");
        current.getChildren().addAll(curLbl, curVal);

        Label arrow = new Label("→");
        arrow.setStyle("-fx-font-size:20px;-fx-text-fill:" + TEXT3 + ";");

        VBox projected = new VBox(2);
        Label projLbl = new Label("Projected GPA");
        projLbl.setStyle("-fx-font-size:10px;-fx-font-family:'Segoe UI';-fx-text-fill:" + TEXT3 + ";");
        Label projVal = new Label(String.format("%.2f", whatIfGpa));
        projVal.setStyle("-fx-font-size:20px;-fx-font-weight:700;-fx-font-family:'Segoe UI';-fx-text-fill:" + GREEN + ";");
        projected.getChildren().addAll(projLbl, projVal);

        VBox change = new VBox(2);
        Label chLbl = new Label("Change");
        chLbl.setStyle("-fx-font-size:10px;-fx-font-family:'Segoe UI';-fx-text-fill:" + TEXT3 + ";");
        Label chVal = new Label(diffText);
        chVal.setStyle("-fx-font-size:20px;-fx-font-weight:700;-fx-font-family:'Segoe UI';-fx-text-fill:" + diffColor + ";");
        change.getChildren().addAll(chLbl, chVal);

        summaryRow.getChildren().addAll(current, arrow, projected, new Region() {{ HBox.setHgrow(this, Priority.ALWAYS); }}, change);
        box.getChildren().add(summaryRow);

        // Letter grade interpretation
        String letterInterp = gpaToLetter(whatIfGpa);
        Label interp = new Label("Projected standing: " + letterInterp);
        interp.setStyle("-fx-font-size:11px;-fx-font-family:'Segoe UI';-fx-text-fill:" + TEXT3 + ";");
        box.getChildren().add(interp);

        return box;
    }

    private void addHypotheticalRow() {
        // Pick a name from courses not already in what-if, or generic
        List<String[]> courses = SessionManager.getCourses();
        String name = "Hypothetical Course";
        for (String[] c : courses) {
            boolean alreadyIn = whatIfRows.stream().anyMatch(r -> r[0].equals(c[0]));
            if (!alreadyIn) { name = c[0]; break; }
        }
        whatIfRows.add(new String[]{ name, "3", "?", "A" });
        refreshWhatIfSection();
        recalcWhatIfGpa();
    }

    private void recalcWhatIfGpa() {
        double totalQP = 0; int totalCr = 0;
        for (String[] r : whatIfRows) {
            int cr = parseCr(r[1]);
            double pts = letterToGpaPoints(r[3].equals("?") ? "A" : r[3]);
            totalQP += pts * cr;
            totalCr += cr;
        }
        double gpa = totalCr == 0 ? 0 : totalQP / totalCr;
        if (whatIfGpaValue != null)
            Platform.runLater(() -> whatIfGpaValue.setText(String.format("%.2f", gpa)));
    }

    // ── Add grade dialog ──────────────────────────────────────────────────────
    private void showAddGradeDialog() {
        javafx.stage.Stage dialog = new javafx.stage.Stage();
        dialog.setTitle("Add Grade");
        dialog.initModality(javafx.stage.Modality.APPLICATION_MODAL);

        VBox form = new VBox(12);
        form.setPadding(new Insets(28));
        form.setStyle("-fx-background-color:" + SURFACE + ";");
        form.setPrefWidth(360);

        Label heading = new Label("Add Grade Record");
        heading.setStyle("-fx-font-size:16px;-fx-font-weight:700;-fx-font-family:'Segoe UI';-fx-text-fill:" + TEXT + ";");

        // Course dropdown from SessionManager
        Label courseLabel = new Label("Course");
        courseLabel.setStyle("-fx-font-size:11px;-fx-font-family:'Segoe UI';-fx-text-fill:" + TEXT3 + ";");
        ComboBox<String> courseBox = styledComboBox();
        List<String[]> courses = SessionManager.getCourses();
        for (String[] c : courses) courseBox.getItems().add(c[0]);
        if (!courseBox.getItems().isEmpty()) courseBox.setValue(courseBox.getItems().get(0));
        if (courseBox.getItems().isEmpty()) courseBox.getItems().add("No courses added yet");

        // Letter grade dropdown
        Label gradeLabel = new Label("Letter Grade");
        gradeLabel.setStyle("-fx-font-size:11px;-fx-font-family:'Segoe UI';-fx-text-fill:" + TEXT3 + ";");
        ComboBox<String> letterBox = styledComboBox();
        letterBox.getItems().addAll("A+","A","A-","B+","B","B-","C+","C","C-","D+","D","F");
        letterBox.setValue("A");

        // Credits
        Label credLabel = new Label("Credits");
        credLabel.setStyle("-fx-font-size:11px;-fx-font-family:'Segoe UI';-fx-text-fill:" + TEXT3 + ";");
        ComboBox<String> credBox = styledComboBox();
        credBox.getItems().addAll("1","2","3","4","5","6");
        credBox.setValue("3");

        // Live GPA preview
        Label previewLbl = new Label("GPA points: 4.0");
        previewLbl.setStyle("-fx-font-size:11px;-fx-font-family:'Segoe UI';-fx-text-fill:" + GREEN + ";");
        letterBox.setOnAction(e -> {
            double pts = letterToGpaPoints(letterBox.getValue());
            previewLbl.setText(String.format("GPA points: %.1f", pts));
        });

        Label errorLbl = new Label("");
        errorLbl.setStyle("-fx-text-fill:" + ROSE + ";-fx-font-size:11px;");
        errorLbl.setVisible(false);

        Button saveBtn = new Button("Save Grade");
        saveBtn.setMaxWidth(Double.MAX_VALUE);
        saveBtn.setStyle("-fx-background-color:" + ACCENT + ";-fx-text-fill:white;-fx-font-size:13px;-fx-font-weight:600;-fx-font-family:'Segoe UI';-fx-background-radius:8;-fx-padding:9 0 9 0;-fx-cursor:hand;");

        saveBtn.setOnAction(e -> {
            String selectedCourse = courseBox.getValue();
            String letter         = letterBox.getValue();
            if (selectedCourse == null || letter == null) {
                errorLbl.setText("Please select a course and grade."); errorLbl.setVisible(true); return;
            }
            String courseId = courses.stream()
                    .filter(c -> c[0].equals(selectedCourse))
                    .map(c -> c.length > 2 ? c[2] : "")
                    .findFirst().orElse("");
            if (courseId.isEmpty()) {
                errorLbl.setText("Course not found. Add a course first."); errorLbl.setVisible(true); return;
            }
            double pts = letterToGpaPoints(letter);
            saveBtn.setDisable(true); saveBtn.setText("Saving...");
            Thread t = new Thread(() -> {
                try {
                    new FirebaseAuthClient().addGradeRecord(courseId, letter, pts, SessionManager.getIdToken());
                    Platform.runLater(() -> { dialog.close(); loadGrades(); });
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Platform.runLater(() -> {
                        saveBtn.setDisable(false); saveBtn.setText("Save Grade");
                        errorLbl.setText("Failed to save."); errorLbl.setVisible(true);
                    });
                }
            });
            t.setDaemon(true); t.start();
        });

        form.getChildren().addAll(heading, courseLabel, courseBox, gradeLabel, letterBox,
                credLabel, credBox, previewLbl, errorLbl, saveBtn);
        dialog.setScene(new javafx.scene.Scene(form));
        dialog.show();
    }

    // ── helpers ───────────────────────────────────────────────────────────────
    private ComboBox<String> styledComboBox() {
        ComboBox<String> cb = new ComboBox<>();
        cb.setMaxWidth(Double.MAX_VALUE);
        cb.setStyle("-fx-background-color:#1f2436;-fx-text-fill:" + TEXT + ";-fx-font-size:13px;-fx-font-family:'Segoe UI';");
        return cb;
    }

    private Label headerLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-size:10px;-fx-font-weight:700;-fx-font-family:'Segoe UI';-fx-text-fill:" + TEXT3 + ";");
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
