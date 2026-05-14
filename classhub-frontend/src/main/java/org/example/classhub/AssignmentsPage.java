package org.example.classhub;

import com.fasterxml.jackson.databind.JsonNode;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import java.util.List;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.ArrayList;

public class AssignmentsPage {

    // ── colours ──────────────────────────────────────────────────────────────
    private static final String BG       = "#0f1117";
    private static final String SURFACE  = "#181c27";
    private static final String BORDER   = "#ffffff12";
    private static final String TEXT     = "#e8eaf2";
    private static final String TEXT2    = "#9097b4";
    private static final String TEXT3    = "#5e6482";
    private static final String ACCENT   = "#6c8ef5";
    private static final String ROSE     = "#f5697b";
    private static final String GREEN    = "#3ecfb0";
    private static final String PURPLE   = "#9f7ffe";
    private static final String AMBER    = "#f5a623";

    // ── state ─────────────────────────────────────────────────────────────────
    // item format: [type, title, course/description, dueDate, completed, id]
    // type = "ASSIGNMENT" | "ANNOUNCEMENT"
    private final ArrayList<String[]> ITEMS = new ArrayList<>();

    private static final String[] FILTERS = {"All","Assignments","Announcements","Completed","In Progress","Overdue"};
    private String activeFilter = "All";

    private VBox  itemList;
    private HBox  filterRow;
    private Scene scene;

    // ── constructor ───────────────────────────────────────────────────────────
    public AssignmentsPage(Stage stage) {
        HBox root = new HBox(0);
        root.setStyle("-fx-background-color:" + BG + ";");
        root.getChildren().addAll(buildSidebar(stage), buildMain());
        scene = new Scene(root, 900, 600);
        loadAssignments();
    }

    // ── load from backend ─────────────────────────────────────────────────────
    private void loadAssignments() {
        if (SessionManager.isDevMode()) return;
        Thread t = new Thread(() -> {
            try {
                FirebaseAuthClient client = new FirebaseAuthClient();
                JsonNode data = client.getAssignments(
                        SessionManager.getUserId(), SessionManager.getIdToken());
                ITEMS.clear();
                for (JsonNode a : data) {
                    ITEMS.add(new String[]{
                        a.path("type").asText("ASSIGNMENT"),
                        a.path("title").asText(),
                        a.path("courseId").asText(),
                        a.path("dueDate").asText(),
                        String.valueOf(a.path("completed").asBoolean()),
                        a.path("id").asText()
                    });
                }
                Platform.runLater(this::refreshList);
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> showListError("Could not load items."));
            }
        });
        t.setDaemon(true);
        t.start();
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
        Label gradesItem = navItem("Grades", false);
        gradesItem.setOnMouseClicked(e -> stage.setScene(ClassHubApplication.gradesScene));
        Label notesItem = navItem("Notes", false);
        notesItem.setOnMouseClicked(e -> stage.setScene(ClassHubApplication.notesScene));

        nav.getChildren().addAll(dashItem, calItem, navItem("Assignments", true), gradesItem, notesItem);
        Label flashItemA = navItem("Flashcards", false);
        flashItemA.setOnMouseClicked(e -> stage.setScene(ClassHubApplication.flashcardsScene));
        nav.getChildren().add(flashItemA);

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

    // ── main area ─────────────────────────────────────────────────────────────
    private VBox buildMain() {
        VBox main = new VBox(0);
        HBox.setHgrow(main, Priority.ALWAYS);

        // Top bar
        HBox topbar = new HBox(10);
        topbar.setPrefHeight(52); topbar.setMinHeight(52);
        topbar.setAlignment(Pos.CENTER_LEFT);
        topbar.setPadding(new Insets(0, 20, 0, 20));
        topbar.setStyle("-fx-background-color:" + BG + ";-fx-border-color:" + BORDER + ";-fx-border-width:0 0 1 0;");

        Label title = new Label("Assignments & Announcements");
        title.setStyle("-fx-font-size:15px;-fx-font-weight:700;-fx-font-family:'Segoe UI';-fx-text-fill:" + TEXT + ";");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button addAssignBtn = new Button("+ Assignment");
        styleAddBtn(addAssignBtn, ACCENT);
        addAssignBtn.setOnAction(e -> showAddDialog("ASSIGNMENT"));

        Button addAnnounceBtn = new Button("+ Announcement");
        styleAddBtn(addAnnounceBtn, PURPLE);
        addAnnounceBtn.setOnAction(e -> showAddDialog("ANNOUNCEMENT"));

        topbar.getChildren().addAll(title, spacer, addAssignBtn, addAnnounceBtn);

        // Content
        VBox content = new VBox(14);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color:" + BG + ";");
        VBox.setVgrow(content, Priority.ALWAYS);

        // Filter chips
        HBox filterContainer = new HBox(10);
        filterContainer.setAlignment(Pos.CENTER_LEFT);
        Label filterLabel = new Label("Filter:");
        filterLabel.setStyle("-fx-font-size:12px;-fx-font-family:'Segoe UI';-fx-text-fill:" + TEXT3 + ";");
        filterRow = new HBox(6);
        for (String f : FILTERS) filterRow.getChildren().add(buildFilterChip(f));
        filterContainer.getChildren().addAll(filterLabel, filterRow);

        // Item list
        itemList = new VBox(8);
        refreshList();

        ScrollPane scroll = new ScrollPane(itemList);
        scroll.setStyle("-fx-background-color:transparent;-fx-background:" + BG + ";");
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setBorder(Border.EMPTY);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        content.getChildren().addAll(filterContainer, scroll);
        main.getChildren().addAll(topbar, content);
        return main;
    }

    // ── filter chips ──────────────────────────────────────────────────────────
    private Label buildFilterChip(String label) {
        boolean active = label.equals(activeFilter);
        Label chip = new Label(label);
        chip.setPadding(new Insets(5, 12, 5, 12));
        chip.setStyle(active
            ? "-fx-background-color:rgba(108,142,245,0.12);-fx-text-fill:" + ACCENT + ";-fx-font-size:11px;-fx-font-family:'Segoe UI';-fx-background-radius:6;-fx-border-color:rgba(108,142,245,0.2);-fx-border-width:1;-fx-border-radius:6;-fx-cursor:hand;"
            : "-fx-background-color:" + SURFACE + ";-fx-text-fill:" + TEXT2 + ";-fx-font-size:11px;-fx-font-family:'Segoe UI';-fx-background-radius:6;-fx-border-color:" + BORDER + ";-fx-border-width:1;-fx-border-radius:6;-fx-cursor:hand;"
        );
        chip.setOnMouseClicked(e -> {
            activeFilter = label;
            filterRow.getChildren().clear();
            for (String f : FILTERS) filterRow.getChildren().add(buildFilterChip(f));
            refreshList();
        });
        return chip;
    }

    // ── list rendering ────────────────────────────────────────────────────────
    private void refreshList() {
        itemList.getChildren().clear();
        boolean anyShown = false;
        for (int i = 0; i < ITEMS.size(); i++) {
            String[] item = ITEMS.get(i);
            if (!passesFilter(item)) continue;
            anyShown = true;
            itemList.getChildren().add(buildItemRow(i, item));
        }
        if (!anyShown) {
            Label empty = new Label("No items to show.");
            empty.setStyle("-fx-font-size:13px;-fx-font-family:'Segoe UI';-fx-text-fill:" + TEXT3 + ";-fx-padding:20 0 0 0;");
            itemList.getChildren().add(empty);
        }
        // Update dashboard stat cards live
        int pending = 0, unread = 0;
        for (String[] item : ITEMS) {
            if ("true".equals(item[4])) continue;
            if ("ANNOUNCEMENT".equals(item[0])) unread++;
            else pending++;
        }
        Dashboard.updateCounts(pending, unread);
    }

    private boolean passesFilter(String[] item) {
        boolean isAssign = "ASSIGNMENT".equals(item[0]);
        boolean isDone   = "true".equals(item[4]);
        boolean overdue  = !isDone && isOverdue(item[3]);
        return switch (activeFilter) {
            case "Assignments"    -> isAssign;
            case "Announcements"  -> !isAssign;
            case "Completed"      -> isDone;
            case "In Progress"    -> !isDone && !overdue;
            case "Overdue"        -> overdue;
            default               -> true;
        };
    }

    private boolean isOverdue(String date) {
        try { return LocalDate.parse(date).isBefore(LocalDate.now()); }
        catch (Exception e) { return false; }
    }

    private HBox buildItemRow(int idx, String[] item) {
        boolean isAssign  = "ASSIGNMENT".equals(item[0]);
        boolean isDone    = "true".equals(item[4]);
        boolean overdue   = !isDone && isOverdue(item[3]);

        String statusColor = isDone ? GREEN : overdue ? ROSE : AMBER;
        String statusText  = isDone ? "COMPLETED" : overdue ? "OVERDUE" : "IN PROGRESS";
        // Announcements use "READ" / "UNREAD" instead
        if (!isAssign) {
            statusColor = isDone ? GREEN : PURPLE;
            statusText  = isDone ? "READ" : "UNREAD";
        }

        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(12, 16, 12, 16));
        row.setStyle("-fx-background-color:" + SURFACE + ";-fx-border-color:" + BORDER + ";-fx-border-width:1;-fx-border-radius:10;-fx-background-radius:10;");

        // ── Checkbox ──────────────────────────────────────────────────────────
        Label checkbox = new Label(isDone ? "✓" : "");
        checkbox.setMinWidth(20); checkbox.setMinHeight(20);
        checkbox.setMaxWidth(20); checkbox.setMaxHeight(20);
        checkbox.setAlignment(Pos.CENTER);
        checkbox.setStyle(isDone
            ? "-fx-background-color:" + (isAssign ? GREEN : PURPLE) + ";-fx-text-fill:white;-fx-font-size:11px;-fx-font-weight:700;-fx-background-radius:4;-fx-cursor:hand;"
            : "-fx-background-color:transparent;-fx-border-color:" + TEXT3 + ";-fx-border-width:1.5;-fx-border-radius:4;-fx-cursor:hand;"
        );
        String tooltip = isAssign ? (isDone ? "Mark incomplete" : "Mark complete")
                                  : (isDone ? "Mark unread"     : "Mark as read");
        Tooltip.install(checkbox, new Tooltip(tooltip));

        checkbox.setOnMouseClicked(e -> {
            String id      = item[5];
            boolean newVal = !isDone;
            item[4] = String.valueOf(newVal);
            // optimistic UI update
            refreshList();
            // sync to backend
            if (!id.isEmpty() && !SessionManager.isDevMode()) {
                Thread t = new Thread(() -> {
                    try {
                        new FirebaseAuthClient().markAssignmentComplete(id, SessionManager.getIdToken());
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        // revert on failure
                        Platform.runLater(() -> { item[4] = String.valueOf(isDone); refreshList(); });
                    }
                });
                t.setDaemon(true);
                t.start();
            }
        });

        // ── Type badge ────────────────────────────────────────────────────────
        Label typeBadge = new Label(isAssign ? "ASSIGNMENT" : "ANNOUNCEMENT");
        typeBadge.setStyle(
            "-fx-background-color:" + (isAssign ? "rgba(108,142,245,0.15)" : "rgba(159,127,254,0.15)") + ";" +
            "-fx-text-fill:" + (isAssign ? ACCENT : PURPLE) + ";" +
            "-fx-font-size:9px;-fx-font-weight:700;-fx-font-family:'Segoe UI';" +
            "-fx-background-radius:4;-fx-padding:2 8 2 8;"
        );

        // ── Status badge ──────────────────────────────────────────────────────
        Label statusBadge = new Label(statusText);
        statusBadge.setStyle(
            "-fx-background-color:" + statusColor + "22;" +
            "-fx-text-fill:" + statusColor + ";" +
            "-fx-font-size:9px;-fx-font-weight:700;-fx-font-family:'Segoe UI';" +
            "-fx-background-radius:4;-fx-padding:2 8 2 8;"
        );

        // ── Info ──────────────────────────────────────────────────────────────
        VBox info = new VBox(3);
        HBox.setHgrow(info, Priority.ALWAYS);

        Label titleLbl = new Label(item[1]);
        titleLbl.setStyle(isDone
            ? "-fx-font-size:13px;-fx-font-weight:600;-fx-font-family:'Segoe UI';-fx-text-fill:" + TEXT3 + ";-fx-strikethrough:true;"
            : "-fx-font-size:13px;-fx-font-weight:600;-fx-font-family:'Segoe UI';-fx-text-fill:" + TEXT + ";"
        );

        String subText = item[2].isEmpty() ? "" : item[2] + "  ·  ";
        subText += item[3].isEmpty() ? "No date" : "Due " + item[3];
        Label subLbl = new Label(subText);
        subLbl.setStyle("-fx-font-size:11px;-fx-font-family:'Segoe UI';-fx-text-fill:" + (isDone ? TEXT3 : TEXT2) + ";");

        info.getChildren().addAll(titleLbl, subLbl);

        row.getChildren().addAll(checkbox, typeBadge, statusBadge, info);
        return row;
    }

    // ── add dialog ────────────────────────────────────────────────────────────
    private void showAddDialog(String type) {
        boolean isAssign = "ASSIGNMENT".equals(type);

        Stage dialog = new Stage();
        dialog.setTitle(isAssign ? "New Assignment" : "New Announcement");
        dialog.initModality(Modality.APPLICATION_MODAL);

        VBox form = new VBox(12);
        form.setPadding(new Insets(28));
        form.setStyle("-fx-background-color:" + SURFACE + ";");
        form.setPrefWidth(360);

        Label heading = new Label(isAssign ? "New Assignment" : "New Announcement");
        heading.setStyle("-fx-font-size:16px;-fx-font-weight:700;-fx-font-family:'Segoe UI';-fx-text-fill:" + TEXT + ";");

        Label typeIndicator = new Label(isAssign ? "ASSIGNMENT" : "ANNOUNCEMENT");
        typeIndicator.setStyle(
            "-fx-background-color:" + (isAssign ? "rgba(108,142,245,0.15)" : "rgba(159,127,254,0.15)") + ";" +
            "-fx-text-fill:" + (isAssign ? ACCENT : PURPLE) + ";" +
            "-fx-font-size:10px;-fx-font-weight:700;-fx-font-family:'Segoe UI';" +
            "-fx-background-radius:4;-fx-padding:3 10 3 10;"
        );

        TextField titleField = styledField("Title");

        // Course / subject dropdown
        Label courseLabel = new Label(isAssign ? "Course" : "From / Subject");
        courseLabel.setStyle("-fx-font-size:11px;-fx-font-family:'Segoe UI';-fx-text-fill:" + TEXT3 + ";");
        ComboBox<String> courseBox = new ComboBox<>();
        courseBox.setMaxWidth(Double.MAX_VALUE);
        courseBox.setStyle("-fx-background-color:#1f2436;-fx-text-fill:" + TEXT + ";-fx-font-size:13px;-fx-font-family:'Segoe UI';");
        List<String[]> courses = SessionManager.getCourses();
        for (String[] c : courses) courseBox.getItems().add(c[0]);
        if (!courseBox.getItems().isEmpty()) courseBox.setValue(courseBox.getItems().get(0));
        else { courseBox.getItems().add("No courses yet"); courseBox.setValue("No courses yet"); }

        // Date picker
        Label dateLabel = new Label("Date");
        dateLabel.setStyle("-fx-font-size:11px;-fx-font-family:'Segoe UI';-fx-text-fill:" + TEXT3 + ";");
        DatePicker datePicker = new DatePicker(LocalDate.now());
        datePicker.setMaxWidth(Double.MAX_VALUE);
        datePicker.setStyle("-fx-background-color:#1f2436;-fx-text-fill:" + TEXT + ";-fx-font-family:'Segoe UI';-fx-font-size:13px;");

        Label errorLbl = new Label("");
        errorLbl.setStyle("-fx-text-fill:" + ROSE + ";-fx-font-size:11px;-fx-font-family:'Segoe UI';");
        errorLbl.setVisible(false);

        Button saveBtn = new Button(isAssign ? "Add Assignment" : "Add Announcement");
        saveBtn.setMaxWidth(Double.MAX_VALUE);
        saveBtn.setStyle(
            "-fx-background-color:" + (isAssign ? ACCENT : PURPLE) + ";" +
            "-fx-text-fill:white;-fx-font-size:13px;-fx-font-weight:600;-fx-font-family:'Segoe UI';" +
            "-fx-background-radius:8;-fx-padding:9 0 9 0;-fx-cursor:hand;"
        );

        saveBtn.setOnAction(e -> {
            String t   = titleField.getText().trim();
            String c   = courseBox.getValue() != null ? courseBox.getValue() : "";
            LocalDate d = datePicker.getValue();

            if (t.isEmpty()) {
                errorLbl.setText("Title is required.");
                errorLbl.setVisible(true);
                return;
            }
            if (d == null) {
                errorLbl.setText("Please select a date.");
                errorLbl.setVisible(true);
                return;
            }

            String dateStr = d.toString(); // yyyy-MM-dd

            // Add to local list immediately
            String localId = "local-" + System.currentTimeMillis();
            ITEMS.add(new String[]{type, t, c, dateStr, "false", localId});
            refreshList();

            // Push to calendar
            SmartCalendarUI.addExternalEvent(d.getYear(), d.getMonthValue(), d.getDayOfMonth(),
                    t, isAssign ? "Assignment" : "Announcement");

            // Sync to backend
            if (!SessionManager.isDevMode()) {
                Thread thread = new Thread(() -> {
                    try {
                        FirebaseAuthClient client = new FirebaseAuthClient();
                        JsonNode result = client.createAssignment(t, c, dateStr, SessionManager.getIdToken());
                        String serverId = result.path("id").asText(localId);
                        // update local id with server id
                        for (String[] item : ITEMS) {
                            if (localId.equals(item[5])) { item[5] = serverId; break; }
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
                thread.setDaemon(true);
                thread.start();
            }

            dialog.close();
        });

        form.getChildren().addAll(heading, typeIndicator, titleField, courseLabel, courseBox,
                                  dateLabel, datePicker, errorLbl, saveBtn);
        dialog.setScene(new javafx.scene.Scene(form));
        dialog.show();
    }

    // ── helpers ───────────────────────────────────────────────────────────────
    private void showListError(String msg) {
        itemList.getChildren().clear();
        Label err = new Label(msg);
        err.setStyle("-fx-font-size:13px;-fx-font-family:'Segoe UI';-fx-text-fill:" + ROSE + ";");
        itemList.getChildren().add(err);
    }

    private void styleAddBtn(Button btn, String color) {
        btn.setStyle(
            "-fx-background-color:rgba(" + hexToRgb(color) + ",0.12);" +
            "-fx-text-fill:" + color + ";" +
            "-fx-font-size:12px;-fx-font-family:'Segoe UI';" +
            "-fx-background-radius:8;-fx-padding:7 12 7 12;-fx-cursor:hand;" +
            "-fx-border-color:rgba(" + hexToRgb(color) + ",0.25);-fx-border-width:1;-fx-border-radius:8;"
        );
    }

    private String hexToRgb(String hex) {
        hex = hex.replace("#", "");
        int r = Integer.parseInt(hex.substring(0,2), 16);
        int g = Integer.parseInt(hex.substring(2,4), 16);
        int b = Integer.parseInt(hex.substring(4,6), 16);
        return r + "," + g + "," + b;
    }

    private TextField styledField(String prompt) {
        TextField f = new TextField();
        f.setPromptText(prompt);
        f.setStyle("-fx-background-color:#1f2436;-fx-text-fill:" + TEXT + ";-fx-prompt-text-fill:" + TEXT3 + ";-fx-border-color:#ffffff20;-fx-border-width:1;-fx-border-radius:8;-fx-background-radius:8;-fx-padding:8 12 8 12;-fx-font-size:13px;-fx-font-family:'Segoe UI';");
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
