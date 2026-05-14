package org.example.classhub;

import com.fasterxml.jackson.databind.JsonNode;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Dashboard {

    private static final String BG      = "#0f1117";
    private static final String SURFACE = "#181c27";
    private static final String BORDER  = "#ffffff12";
    private static final String TEXT    = "#e8eaf2";
    private static final String TEXT2   = "#9097b4";
    private static final String TEXT3   = "#5e6482";
    private static final String ACCENT  = "#6c8ef5";
    private static final String ROSE    = "#f5697b";
    private static final String GREEN   = "#3ecfb0";
    private static final String PURPLE  = "#9f7ffe";

    // CLASSES format: [courseName, schedule, id]
    private static String[][] CLASSES = new String[0][];

    private Label gpaValue;
    private Label pendingValue;
    private Label announcementValue;
    private VBox  classGrid;
    private Scene scene;

    // Shared counters — updated by AssignmentsPage via static methods
    private static int pendingCount       = 0;
    private static int announcementCount  = 0;
    private static Dashboard instance;

    public Dashboard(Stage stage) {
        instance = this;
        HBox root = new HBox(0);
        root.setStyle("-fx-background-color:" + BG + ";");
        root.getChildren().addAll(buildSidebar(stage), buildMain());
        scene = new Scene(root, 900, 600);
        loadDashboard();
    }

    // ── Called by AssignmentsPage whenever items change ───────────────────────
    public static void updateCounts(int pending, int announcements) {
        pendingCount      = pending;
        announcementCount = announcements;
        if (instance != null) {
            Platform.runLater(() -> {
                if (instance.pendingValue      != null) instance.pendingValue.setText(String.valueOf(pending));
                if (instance.announcementValue != null) instance.announcementValue.setText(String.valueOf(announcements));
            });
        }
    }

    // ── Sidebar ───────────────────────────────────────────────────────────────
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
        nav.getChildren().add(navItem("Dashboard", true));

        Label calItem = navItem("Smart Calendar", false);
        calItem.setOnMouseClicked(e -> stage.setScene(ClassHubApplication.calendarScene));
        Label assignItem = navItem("Assignments", false);
        assignItem.setOnMouseClicked(e -> stage.setScene(ClassHubApplication.assignmentsScene));
        Label gradesItem = navItem("Grades", false);
        gradesItem.setOnMouseClicked(e -> stage.setScene(ClassHubApplication.gradesScene));
        Label notesItem = navItem("Notes", false);
        notesItem.setOnMouseClicked(e -> stage.setScene(ClassHubApplication.notesScene));
        Label flashItem = navItem("Flashcards", false);
        flashItem.setOnMouseClicked(e -> stage.setScene(ClassHubApplication.flashcardsScene));
        nav.getChildren().addAll(calItem, assignItem, gradesItem, notesItem, flashItem);

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

    // ── Main content ──────────────────────────────────────────────────────────
    private VBox buildMain() {
        VBox main = new VBox(0);
        HBox.setHgrow(main, Priority.ALWAYS);

        HBox topbar = new HBox();
        topbar.setPrefHeight(52); topbar.setMinHeight(52);
        topbar.setAlignment(Pos.CENTER_LEFT);
        topbar.setPadding(new Insets(0, 20, 0, 20));
        topbar.setStyle("-fx-background-color:" + BG + ";-fx-border-color:" + BORDER + ";-fx-border-width:0 0 1 0;");
        Label pageTitle = new Label("Dashboard");
        pageTitle.setStyle("-fx-font-size:15px;-fx-font-weight:700;-fx-font-family:'Segoe UI';-fx-text-fill:" + TEXT + ";");
        topbar.getChildren().add(pageTitle);

        VBox content = new VBox(16);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color:" + BG + ";");

        ScrollPane scroll = new ScrollPane(content);
        scroll.setStyle("-fx-background-color:transparent;-fx-background:" + BG + ";");
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setBorder(Border.EMPTY);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        VBox classSection = buildClassesSection();
        VBox.setVgrow(classSection, Priority.ALWAYS);
        content.getChildren().addAll(buildStatRow(), classSection);
        main.getChildren().addAll(topbar, scroll);
        return main;
    }

    // ── Stat cards ────────────────────────────────────────────────────────────
    private HBox buildStatRow() {
        HBox row = new HBox(12);

        VBox c1 = statCard("GPA", "...", ACCENT);
        gpaValue = (Label) c1.getChildren().get(1);

        VBox c2 = statCard("Pending Tasks", String.valueOf(pendingCount), ROSE);
        pendingValue = (Label) c2.getChildren().get(1);

        VBox c3 = statCard("Announcements", String.valueOf(announcementCount), PURPLE);
        announcementValue = (Label) c3.getChildren().get(1);

        for (VBox c : new VBox[]{c1, c2, c3}) {
            HBox.setHgrow(c, Priority.ALWAYS);
            c.setMaxWidth(Double.MAX_VALUE);
        }
        row.getChildren().addAll(c1, c2, c3);
        return row;
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

    // ── Classes section ───────────────────────────────────────────────────────
    private VBox buildClassesSection() {
        VBox outerCard = new VBox(12);
        outerCard.setPadding(new Insets(18, 20, 18, 20));
        outerCard.setStyle("-fx-background-color:" + SURFACE + ";-fx-border-color:" + BORDER + ";-fx-border-width:1;-fx-border-radius:12;-fx-background-radius:12;");

        Label title = new Label("My Classes");
        title.setStyle("-fx-font-size:13px;-fx-font-weight:700;-fx-font-family:'Segoe UI';-fx-text-fill:" + TEXT + ";");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button addBtn = new Button("+ Add Course");
        addBtn.setStyle("-fx-background-color:rgba(108,142,245,0.12);-fx-text-fill:" + ACCENT + ";-fx-font-size:11px;-fx-font-family:'Segoe UI';-fx-background-radius:6;-fx-padding:5 10 5 10;-fx-cursor:hand;-fx-border-color:rgba(108,142,245,0.2);-fx-border-width:1;-fx-border-radius:6;");
        addBtn.setOnAction(e -> showAddCourseDialog());

        HBox titleRow = new HBox();
        titleRow.setAlignment(Pos.CENTER_LEFT);
        titleRow.getChildren().addAll(title, spacer, addBtn);

        classGrid = new VBox(10);
        refreshClassGrid();

        outerCard.getChildren().addAll(titleRow, classGrid);
        return outerCard;
    }

    private VBox classCard(String name, String schedule, String id) {
        VBox card = new VBox(6);
        card.setPadding(new Insets(14, 20, 14, 20));
        card.setMinHeight(90);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle("-fx-background-color:" + BG + ";-fx-border-color:" + BORDER + ";-fx-border-width:1;-fx-border-radius:8;-fx-background-radius:8;-fx-cursor:hand;");

        Label nameLbl = new Label(name);
        nameLbl.setStyle("-fx-font-size:15px;-fx-font-weight:700;-fx-font-family:'Segoe UI';-fx-text-fill:" + TEXT + ";");

        Label schedLbl = new Label(schedule.isEmpty() ? "No schedule set" : schedule);
        schedLbl.setStyle("-fx-font-size:11px;-fx-font-family:'Segoe UI';-fx-text-fill:" + TEXT3 + ";");
        schedLbl.setWrapText(true);

        card.getChildren().addAll(nameLbl, schedLbl);
        return card;
    }

    private void refreshClassGrid() {
        classGrid.getChildren().clear();
        int cols = 2;
        int rows = (int) Math.ceil(CLASSES.length / (double) cols);
        for (int r = 0; r < rows; r++) {
            HBox row = new HBox(10);
            for (int c = 0; c < cols; c++) {
                int idx = r * cols + c;
                if (idx < CLASSES.length) {
                    VBox card = classCard(CLASSES[idx][0], CLASSES[idx][1],
                                         CLASSES[idx].length > 2 ? CLASSES[idx][2] : "");
                    HBox.setHgrow(card, Priority.ALWAYS);
                    card.setMaxWidth(Double.MAX_VALUE);
                    row.getChildren().add(card);
                } else {
                    Region filler = new Region();
                    HBox.setHgrow(filler, Priority.ALWAYS);
                    row.getChildren().add(filler);
                }
            }
            classGrid.getChildren().add(row);
        }
        if (CLASSES.length == 0) {
            Label empty = new Label("No courses added yet. Click + Add Course to get started.");
            empty.setStyle("-fx-font-size:12px;-fx-font-family:'Segoe UI';-fx-text-fill:" + TEXT3 + ";-fx-padding:10 0 0 0;");
            classGrid.getChildren().add(empty);
        }
    }

    // ── Add Course dialog ─────────────────────────────────────────────────────
    private void showAddCourseDialog() {
        javafx.stage.Stage dialog = new javafx.stage.Stage();
        dialog.setTitle("Add Course");
        dialog.initModality(javafx.stage.Modality.APPLICATION_MODAL);

        VBox form = new VBox(14);
        form.setPadding(new Insets(28));
        form.setStyle("-fx-background-color:" + SURFACE + ";");
        form.setPrefWidth(420);

        Label heading = new Label("New Course");
        heading.setStyle("-fx-font-size:16px;-fx-font-weight:700;-fx-font-family:'Segoe UI';-fx-text-fill:" + TEXT + ";");

        TextField nameField = styledField("Course Name (e.g. Software Engineering)");
        TextField codeField = styledField("Course Code (e.g. CSC325)");

        // ── Time field ────────────────────────────────────────────────────────
        Label timeLabel = new Label("Class Time");
        timeLabel.setStyle("-fx-font-size:11px;-fx-font-family:'Segoe UI';-fx-text-fill:" + TEXT3 + ";");
        TextField timeField = styledField("e.g. 9:00 AM – 10:15 AM");

        // ── Day selector ──────────────────────────────────────────────────────
        Label daysLabel = new Label("Class Days");
        daysLabel.setStyle("-fx-font-size:11px;-fx-font-family:'Segoe UI';-fx-text-fill:" + TEXT3 + ";");

        String[] dayNames = {"Mon", "Tue", "Wed", "Thu", "Fri"};
        boolean[] daySelected = {false, false, false, false, false};

        HBox dayRow = new HBox(8);
        dayRow.setAlignment(Pos.CENTER_LEFT);
        Label[] dayBtns = new Label[5];
        for (int i = 0; i < 5; i++) {
            int fi = i;
            Label btn = new Label(dayNames[i]);
            btn.setPadding(new Insets(6, 14, 6, 14));
            btn.setStyle(inactiveDayStyle());
            btn.setOnMouseClicked(e -> {
                daySelected[fi] = !daySelected[fi];
                btn.setStyle(daySelected[fi] ? activeDayStyle() : inactiveDayStyle());
            });
            dayBtns[i] = btn;
            dayRow.getChildren().add(btn);
        }

        // Quick-pick buttons
        HBox quickRow = new HBox(8);
        quickRow.setAlignment(Pos.CENTER_LEFT);
        Label mwfBtn = quickPickLabel("MWF");
        mwfBtn.setOnMouseClicked(e -> {
            boolean[] mwf = {true, false, true, false, true};
            applyDays(daySelected, dayBtns, mwf);
        });
        Label tthBtn = quickPickLabel("TTh");
        tthBtn.setOnMouseClicked(e -> {
            boolean[] tth = {false, true, false, true, false};
            applyDays(daySelected, dayBtns, tth);
        });
        Label mfBtn = quickPickLabel("M–F");
        mfBtn.setOnMouseClicked(e -> {
            boolean[] mf = {true, true, true, true, true};
            applyDays(daySelected, dayBtns, mf);
        });
        quickRow.getChildren().addAll(new Label("Quick:") {{
            setStyle("-fx-font-size:11px;-fx-font-family:'Segoe UI';-fx-text-fill:" + TEXT3 + ";");
        }}, mwfBtn, tthBtn, mfBtn);

        Label errorLbl = new Label("");
        errorLbl.setStyle("-fx-text-fill:" + ROSE + ";-fx-font-size:11px;-fx-font-family:'Segoe UI';");
        errorLbl.setVisible(false);

        Button saveBtn = new Button("Add Course");
        saveBtn.setMaxWidth(Double.MAX_VALUE);
        saveBtn.setStyle("-fx-background-color:" + ACCENT + ";-fx-text-fill:white;-fx-font-size:13px;-fx-font-family:'Segoe UI';-fx-background-radius:8;-fx-padding:9 0 9 0;-fx-cursor:hand;");

        saveBtn.setOnAction(e -> {
            String name = nameField.getText().trim();
            String code = codeField.getText().trim();
            String time = timeField.getText().trim();

            if (name.isEmpty() || code.isEmpty()) {
                errorLbl.setText("Name and code are required.");
                errorLbl.setVisible(true);
                return;
            }

            // Build schedule string from selected days + time
            StringBuilder schedSb = new StringBuilder();
            String[] abbrevs = {"Mon", "Tue", "Wed", "Thu", "Fri"};
            for (int i = 0; i < 5; i++) {
                if (daySelected[i]) {
                    if (schedSb.length() > 0) schedSb.append("/");
                    schedSb.append(abbrevs[i]);
                }
            }
            String schedule = schedSb.toString();
            if (!time.isEmpty()) schedule += (schedule.isEmpty() ? "" : " ") + time;

            // Add class events to calendar for the next 16 weeks
            DayOfWeek[] selectedDows = new DayOfWeek[]{
                DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                DayOfWeek.THURSDAY, DayOfWeek.FRIDAY
            };
            String calTime = time.isEmpty() ? "Class" : time;
            String finalName = name;
            LocalDate today = LocalDate.now();
            for (int week = 0; week < 16; week++) {
                for (int di = 0; di < 5; di++) {
                    if (!daySelected[di]) continue;
                    LocalDate classDate = today.with(
                        java.time.temporal.TemporalAdjusters.nextOrSame(selectedDows[di])
                    ).plusWeeks(week);
                    SmartCalendarUI.addExternalEvent(
                        classDate.getYear(), classDate.getMonthValue(), classDate.getDayOfMonth(),
                        finalName, calTime
                    );
                }
            }

            saveBtn.setDisable(true);
            saveBtn.setText("Saving...");
            String finalSchedule = schedule;

            Thread thread = new Thread(() -> {
                try {
                    FirebaseAuthClient client = new FirebaseAuthClient();
                    JsonNode result = client.createCourse(name, code, "Spring 2026", 3,
                            SessionManager.getIdToken());

                    String newId = result.path("id").asText("");
                    List<String[]> updatedCourses = new ArrayList<>(SessionManager.getCourses());
                    updatedCourses.add(new String[]{name, code, newId});
                    SessionManager.setCourses(updatedCourses);

                    CLASSES = updatedCourses.stream()
                            .map(c -> new String[]{c[0], finalSchedule, c[2]})
                            .toArray(String[][]::new);

                    Platform.runLater(() -> { refreshClassGrid(); dialog.close(); });
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Platform.runLater(() -> {
                        saveBtn.setDisable(false);
                        saveBtn.setText("Add Course");
                        errorLbl.setText("Failed to save. Is the server running?");
                        errorLbl.setVisible(true);
                    });
                }
            });
            thread.setDaemon(true);
            thread.start();
        });

        form.getChildren().addAll(heading, nameField, codeField,
                timeLabel, timeField, daysLabel, dayRow, quickRow,
                errorLbl, saveBtn);
        dialog.setScene(new javafx.scene.Scene(form));
        dialog.show();
    }

    private void applyDays(boolean[] selected, Label[] btns, boolean[] values) {
        for (int i = 0; i < 5; i++) {
            selected[i] = values[i];
            btns[i].setStyle(values[i] ? activeDayStyle() : inactiveDayStyle());
        }
    }

    private String activeDayStyle() {
        return "-fx-background-color:" + ACCENT + ";-fx-text-fill:white;-fx-font-size:12px;" +
               "-fx-font-weight:700;-fx-font-family:'Segoe UI';-fx-background-radius:6;" +
               "-fx-padding:6 14 6 14;-fx-cursor:hand;";
    }

    private String inactiveDayStyle() {
        return "-fx-background-color:#1f2436;-fx-text-fill:" + TEXT3 + ";-fx-font-size:12px;" +
               "-fx-font-family:'Segoe UI';-fx-background-radius:6;" +
               "-fx-border-color:#ffffff20;-fx-border-width:1;-fx-border-radius:6;" +
               "-fx-padding:6 14 6 14;-fx-cursor:hand;";
    }

    private Label quickPickLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-background-color:rgba(108,142,245,0.12);-fx-text-fill:" + ACCENT + ";" +
                   "-fx-font-size:11px;-fx-font-family:'Segoe UI';-fx-background-radius:5;" +
                   "-fx-padding:4 10 4 10;-fx-cursor:hand;" +
                   "-fx-border-color:rgba(108,142,245,0.2);-fx-border-width:1;-fx-border-radius:5;");
        return l;
    }

    // ── Load dashboard data ───────────────────────────────────────────────────
    private void loadDashboard() {
        if (SessionManager.isDevMode()) return;
        Thread t = new Thread(() -> {
            try {
                FirebaseAuthClient client = new FirebaseAuthClient();

                // Courses
                JsonNode coursesJson = client.getCourses(SessionManager.getUserId(), SessionManager.getIdToken());
                List<String[]> courseList = new ArrayList<>();
                for (JsonNode c : coursesJson) {
                    courseList.add(new String[]{
                        c.path("courseName").asText("Unknown"),
                        c.path("courseCode").asText(""),
                        c.path("id").asText()
                    });
                }
                SessionManager.setCourses(courseList);
                CLASSES = courseList.stream()
                        .map(c -> new String[]{c[0], c[1], c[2]})
                        .toArray(String[][]::new);

                // GPA
                JsonNode gpa = client.getGpaSummary(SessionManager.getUserId(), SessionManager.getIdToken());
                double gpaVal = gpa.path("gpa").asDouble(0.0);

                // Assignments + announcements
                JsonNode assignments = client.getAssignments(SessionManager.getUserId(), SessionManager.getIdToken());
                int pending = 0, unreadAnnouncements = 0;
                for (JsonNode a : assignments) {
                    boolean done = a.path("completed").asBoolean();
                    String type  = a.path("type").asText("ASSIGNMENT");
                    if (!done) {
                        if ("ANNOUNCEMENT".equals(type)) unreadAnnouncements++;
                        else pending++;
                    }
                }
                int finalPending = pending;
                int finalAnnounce = unreadAnnouncements;
                double finalGpa   = gpaVal;

                Platform.runLater(() -> {
                    gpaValue.setText(String.format("%.2f", finalGpa));
                    pendingValue.setText(String.valueOf(finalPending));
                    announcementValue.setText(String.valueOf(finalAnnounce));
                    pendingCount      = finalPending;
                    announcementCount = finalAnnounce;
                    refreshClassGrid();
                });

            } catch (Exception e) { e.printStackTrace(); }
        });
        t.setDaemon(true);
        t.start();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────
    private TextField styledField(String prompt) {
        TextField f = new TextField();
        f.setPromptText(prompt);
        f.setStyle("-fx-background-color:#1f2436;-fx-text-fill:" + TEXT + ";-fx-prompt-text-fill:" + TEXT3 + ";" +
                   "-fx-border-color:#ffffff20;-fx-border-width:1;-fx-border-radius:8;-fx-background-radius:8;" +
                   "-fx-padding:8 12 8 12;-fx-font-size:13px;-fx-font-family:'Segoe UI';");
        return f;
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

    public Scene getScene() { return scene; }
}
