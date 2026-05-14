package org.example.classhub;


import com.fasterxml.jackson.databind.JsonNode;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.*;

public class SmartCalendarUI {

    private Scene scene;

    private static final int TODAY_YEAR  = LocalDate.now().getYear();
    private static final int TODAY_MONTH = LocalDate.now().getMonthValue();
    private static final int TODAY_DAY   = LocalDate.now().getDayOfMonth();

    private int currentYear  = TODAY_YEAR;
    private int currentMonth = TODAY_MONTH;

    private VBox popup;
    private GridPane grid;
    private StackPane contentStack;
    private Label monthLabel;

    private static final Map<String, List<String[]>> EVENTS = new HashMap<>();

    private static String eKey(int y, int m, int d) { return y + "-" + m + "-" + d; }

    private static void se(int y, int m, int d, String title, String time) { putEvent(eKey(y,m,d), title, time); }

    private static void putEvent(String key, String title, String time) {
        EVENTS.computeIfAbsent(key, k -> new ArrayList<>()).add(new String[]{title, time});
        EVENTS.get(key).sort((a, b) -> { int c = a[1].compareTo(b[1]); return c != 0 ? c : a[0].compareToIgnoreCase(b[0]); });
    }

    private void addEvent(int day, String title, String time) { putEvent(eKey(currentYear, currentMonth, day), title, time); }

    /**
     * Called by AssignmentsPage when a new assignment or announcement is created.
     * Adds it to the shared EVENTS map so it shows on the calendar immediately.
     * "type" is shown as the time label (e.g. "Assignment" or "Announcement").
     */
    public static void addExternalEvent(int year, int month, int day, String title, String type) {
        putEvent(eKey(year, month, day), title, type);
    }

    private List<String[]> getEvents(int day) {
        return new ArrayList<>(EVENTS.getOrDefault(eKey(currentYear, currentMonth, day), new ArrayList<>()));
    }

    private void removeEvent(int day, String title, String time) {
        String k = eKey(currentYear, currentMonth, day);
        List<String[]> list = EVENTS.get(k);
        if (list != null) { list.removeIf(e -> e[0].equals(title) && e[1].equals(time)); if (list.isEmpty()) EVENTS.remove(k); }
    }

    private int daysInMonth() { return YearMonth.of(currentYear, currentMonth).lengthOfMonth(); }
    private int startCol()    { return LocalDate.of(currentYear, currentMonth, 1).getDayOfWeek().getValue() % 7; }
    private boolean isToday(int day) { return currentYear==TODAY_YEAR && currentMonth==TODAY_MONTH && day==TODAY_DAY; }

    private String monthName() {
        return LocalDate.of(currentYear, currentMonth, 1).getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " " + currentYear;
    }

    public SmartCalendarUI(Stage stage) {
        HBox root = new HBox(0);
        root.setStyle("-fx-background-color:#0f1117;");
        root.getChildren().addAll(buildSidebar(stage), buildMain());
        scene = new Scene(root, 900, 600);
        loadAssignmentsIntoCalendar();
    }

    private VBox buildSidebar(Stage stage) {
        VBox sidebar = new VBox(0);
        sidebar.setMinWidth(200); sidebar.setMaxWidth(200);
        sidebar.setStyle("-fx-background-color:#181c27;-fx-border-color:#ffffff12;-fx-border-width:0 1 0 0;");

        VBox logo = new VBox(3);
        logo.setPadding(new Insets(20, 16, 16, 16));
        logo.setStyle("-fx-border-color:#ffffff12;-fx-border-width:0 0 1 0;");
        Label logoTitle = new Label("ClassHub");
        logoTitle.setStyle("-fx-font-size:18px;-fx-font-weight:700;-fx-font-family:'Segoe UI';-fx-text-fill:#e8eaf2;");
        Label logoSub = new Label("STUDENT PORTAL");
        logoSub.setStyle("-fx-font-size:9px;-fx-font-family:'Segoe UI';-fx-text-fill:#5e6482;");
        logo.getChildren().addAll(logoTitle, logoSub);

        VBox nav = new VBox(2);
        nav.setPadding(new Insets(12, 8, 12, 8));
        VBox.setVgrow(nav, Priority.ALWAYS);
        Label section = new Label("MAIN");
        section.setStyle("-fx-font-size:9px;-fx-font-family:'Segoe UI';-fx-text-fill:#5e6482;-fx-padding:10 10 6 10;");
        nav.getChildren().add(section);

        Label dashItem = navItem("Dashboard", false);
        dashItem.setOnMouseClicked(e -> stage.setScene(ClassHubApplication.dashboardScene));
        nav.getChildren().add(dashItem);
        nav.getChildren().add(navItem("Smart Calendar", true));

        Label assignItem = navItem("Assignments", false);
        assignItem.setOnMouseClicked(e -> stage.setScene(ClassHubApplication.assignmentsScene));
        nav.getChildren().add(assignItem);

        Label gradesItem = navItem("Grades", false);
        gradesItem.setOnMouseClicked(e -> stage.setScene(ClassHubApplication.gradesScene));
        nav.getChildren().add(gradesItem);

        Label notesItem = navItem("Notes", false);
        notesItem.setOnMouseClicked(e -> stage.setScene(ClassHubApplication.notesScene));
        nav.getChildren().add(notesItem);

        VBox footer = new VBox(8);
        footer.setPadding(new Insets(12));
        footer.setStyle("-fx-border-color:#ffffff12;-fx-border-width:1 0 0 0;");
        HBox userRow = new HBox(8);
        userRow.setAlignment(Pos.CENTER_LEFT);
        Label initials = new Label(SessionManager.getInitials());
        initials.setMinWidth(32); initials.setMinHeight(32);
        initials.setMaxWidth(32); initials.setMaxHeight(32);
        initials.setAlignment(Pos.CENTER);
        initials.setStyle("-fx-background-color:rgba(108,142,245,0.2);-fx-text-fill:#6c8ef5;-fx-font-size:11px;-fx-font-weight:700;-fx-font-family:'Segoe UI';-fx-background-radius:50;");
        VBox userInfo = new VBox(1);
        Label userName = new Label(SessionManager.getFullName());
        userName.setStyle("-fx-font-size:12px;-fx-font-weight:600;-fx-font-family:'Segoe UI';-fx-text-fill:#e8eaf2;");
        Label userRole = new Label(SessionManager.getRole());
        userRole.setStyle("-fx-font-size:10px;-fx-font-family:'Segoe UI';-fx-text-fill:#5e6482;");
        userInfo.getChildren().addAll(userName, userRole);
        userRow.getChildren().addAll(initials, userInfo);
        Button logoutBtn = new Button("Logout");
        logoutBtn.setMaxWidth(Double.MAX_VALUE);
        logoutBtn.setStyle("-fx-background-color:rgba(245,105,123,0.1);-fx-text-fill:#f5697b;-fx-font-size:12px;-fx-font-family:'Segoe UI';-fx-background-radius:8;-fx-padding:7 12 7 12;-fx-cursor:hand;-fx-border-color:rgba(245,105,123,0.2);-fx-border-width:1;-fx-border-radius:8;");
        logoutBtn.setOnAction(e -> {
            SessionManager.logout();
            stage.setScene(ClassHubApplication.loginScene);
        });
        footer.getChildren().addAll(userRow, logoutBtn);
        sidebar.getChildren().addAll(logo, nav, footer);
        return sidebar;
    }

    private VBox buildMain() {
        VBox main = new VBox(0);
        HBox.setHgrow(main, Priority.ALWAYS);

        HBox topbar = new HBox();
        topbar.setPrefHeight(52); topbar.setMinHeight(52);
        topbar.setAlignment(Pos.CENTER_LEFT);
        topbar.setPadding(new Insets(0, 20, 0, 20));
        topbar.setStyle("-fx-background-color:#0f1117;-fx-border-color:#ffffff12;-fx-border-width:0 0 1 0;");
        Label title = new Label("Smart Calendar");
        title.setStyle("-fx-font-size:15px;-fx-font-weight:700;-fx-font-family:'Segoe UI';-fx-text-fill:#e8eaf2;");
        topbar.getChildren().add(title);

        VBox content = new VBox(16);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color:#0f1117;");
        VBox.setVgrow(content, Priority.ALWAYS);

        HBox monthRow = new HBox(12);
        monthRow.setAlignment(Pos.CENTER_LEFT);
        String navBtnStyle = "-fx-background-color:#1f2436;-fx-text-fill:#9097b4;-fx-font-size:13px;-fx-font-family:'Segoe UI';-fx-background-radius:8;-fx-padding:6 10 6 10;-fx-cursor:hand;-fx-border-color:#ffffff12;-fx-border-width:1;-fx-border-radius:8;";
        Button prevBtn = new Button("←"); prevBtn.setStyle(navBtnStyle);
        Button nextBtn = new Button("→"); nextBtn.setStyle(navBtnStyle);
        monthLabel = new Label(monthName());
        monthLabel.setStyle("-fx-font-size:15px;-fx-font-weight:500;-fx-font-family:'Segoe UI';-fx-text-fill:#e8eaf2;");

        prevBtn.setOnAction(e -> navigate(-1));
        nextBtn.setOnAction(e -> navigate(1));

        Button addBtn = new Button("+ Event");
        addBtn.setStyle("-fx-background-color:rgba(108,142,245,0.12);-fx-text-fill:#6c8ef5;-fx-font-size:12px;-fx-font-family:'Segoe UI';-fx-background-radius:8;-fx-padding:7 14 7 14;-fx-cursor:hand;-fx-border-color:rgba(108,142,245,0.2);-fx-border-width:1;-fx-border-radius:8;");
        addBtn.setOnAction(e -> showAddEventDialog());
        Button studyBtn = new Button("+ Study Time");
        studyBtn.setStyle("-fx-background-color:rgba(62,207,176,0.12);-fx-text-fill:#3ecfb0;-fx-font-size:12px;-fx-font-family:'Segoe UI';-fx-background-radius:8;-fx-padding:7 14 7 14;-fx-cursor:hand;-fx-border-color:rgba(62,207,176,0.2);-fx-border-width:1;-fx-border-radius:8;");
        studyBtn.setOnAction(e -> showStudyTimeDialog());
        Button todayBtn = new Button("Today");
        todayBtn.setStyle("-fx-background-color:rgba(108,142,245,0.12);-fx-text-fill:#6c8ef5;-fx-font-size:12px;-fx-font-family:'Segoe UI';-fx-background-radius:8;-fx-padding:7 14 7 14;-fx-cursor:hand;-fx-border-color:rgba(108,142,245,0.2);-fx-border-width:1;-fx-border-radius:8;");
        todayBtn.setOnAction(e -> { currentYear = TODAY_YEAR; currentMonth = TODAY_MONTH; monthLabel.setText(monthName()); rebuildGrid(); });
        HBox spacer = new HBox(); HBox.setHgrow(spacer, Priority.ALWAYS);
        monthRow.getChildren().addAll(prevBtn, monthLabel, nextBtn, spacer, addBtn, studyBtn, todayBtn);
        content.getChildren().add(monthRow);

        contentStack = new StackPane();
        VBox.setVgrow(contentStack, Priority.ALWAYS);
        contentStack.setOnMouseClicked(e -> {
            if (popup != null) { contentStack.getChildren().remove(popup); popup = null; }
        });

        VBox calOuter = new VBox(0);
        calOuter.setStyle("-fx-background-color:#181c27;-fx-border-color:#ffffff12;-fx-border-width:1;-fx-border-radius:12;-fx-background-radius:12;");
        VBox.setVgrow(calOuter, Priority.ALWAYS);

        HBox dayHeaders = new HBox(0);
        for (String d : new String[]{"Sun","Mon","Tue","Wed","Thu","Fri","Sat"}) {
            Label lbl = new Label(d);
            lbl.setMaxWidth(Double.MAX_VALUE);
            lbl.setAlignment(Pos.CENTER);
            lbl.setStyle("-fx-font-size:11px;-fx-font-weight:500;-fx-font-family:'Segoe UI';-fx-text-fill:#5e6482;-fx-border-color:#ffffff12;-fx-border-width:0 0 1 0;-fx-padding:10 0 10 0;");
            HBox.setHgrow(lbl, Priority.ALWAYS);
            dayHeaders.getChildren().add(lbl);
        }
        calOuter.getChildren().add(dayHeaders);

        grid = new GridPane();
        grid.setMaxWidth(Double.MAX_VALUE);
        grid.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(grid, Priority.ALWAYS);
        for (int c = 0; c < 7; c++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(100.0 / 7);
            grid.getColumnConstraints().add(cc);
        }

        rebuildGrid();
        calOuter.getChildren().add(grid);
        content.getChildren().add(calOuter);
        contentStack.getChildren().add(content);
        main.getChildren().addAll(topbar, contentStack);
        return main;
    }

    private void navigate(int delta) {
        currentMonth += delta;
        if (currentMonth < 1)  { currentMonth = 12; currentYear--; }
        else if (currentMonth > 12) { currentMonth = 1; currentYear++; }
        if (popup != null) { contentStack.getChildren().remove(popup); popup = null; }
        monthLabel.setText(monthName());
        rebuildGrid();
    }

    private void rebuildGrid() {
        grid.getChildren().clear();
        grid.getRowConstraints().clear();

        int sc  = startCol();
        int dim = daysInMonth();
        int numRows   = (int) Math.ceil((sc + dim) / 7.0);
        int nextCount = numRows * 7 - sc - dim;

        for (int r = 0; r < numRows; r++) {
            RowConstraints rc = new RowConstraints();
            rc.setMinHeight(90);
            rc.setVgrow(Priority.ALWAYS);
            rc.setPercentHeight(100.0 / numRows);
            grid.getRowConstraints().add(rc);
        }

        int prevLen = YearMonth.of(currentYear, currentMonth).minusMonths(1).lengthOfMonth();
        for (int i = 0; i < sc; i++) {
            grid.add(buildCell(prevLen - sc + 1 + i, false, true, false), i, 0);
        }

        int col = sc, row = 0;
        for (int d = 1; d <= dim; d++) {
            List<String[]> evts = getEvents(d);
            boolean hasStudy = evts.stream().anyMatch(e -> e[0].toLowerCase().contains("study"));
            VBox cell = buildCell(d, isToday(d), false, hasStudy);

            for (int i = 0; i < Math.min(evts.size(), 2); i++) {
                String[] evt = evts.get(i);
                boolean study = evt[0].toLowerCase().contains("study");
                Label pill = new Label(formatTime(evt[1]) + " " + evt[0]);
                pill.setMaxWidth(Double.MAX_VALUE);
                pill.setTextOverrun(OverrunStyle.ELLIPSIS);
                pill.setStyle((study
                        ? "-fx-background-color:rgba(62,207,176,0.15);-fx-text-fill:#3ecfb0;"
                        : "-fx-background-color:rgba(245,105,123,0.15);-fx-text-fill:#f5697b;") +
                        "-fx-font-size:12px;-fx-font-family:'Segoe UI';-fx-background-radius:4;-fx-padding:4 8 4 8;");
                cell.getChildren().add(pill);
            }
            if (evts.size() > 2) {
                Label more = new Label("+" + (evts.size() - 2) + " more");
                more.setMaxWidth(Double.MAX_VALUE);
                more.setStyle("-fx-background-color:rgba(108,142,245,0.12);-fx-text-fill:#6c8ef5;-fx-font-size:10px;-fx-font-family:'Segoe UI';-fx-background-radius:4;-fx-padding:2 8 2 8;");
                cell.getChildren().add(more);
            }
            if (!evts.isEmpty()) {
                final int day = d;
                cell.setOnMouseClicked(e -> { e.consume(); showPopup(day); });
                cell.setStyle(cell.getStyle() + "-fx-cursor:hand;");
            }
            grid.add(cell, col, row);
            col++;
            if (col == 7) { col = 0; row++; }
        }

        for (int nd = 1; nd <= nextCount; nd++) {
            grid.add(buildCell(nd, false, true, false), col++, row);
        }
    }

    private void showPopup(int day) {
        if (popup != null) contentStack.getChildren().remove(popup);

        String monthShort = LocalDate.of(currentYear, currentMonth, 1).getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);

        popup = new VBox(10);
        popup.setStyle("-fx-background-color:#1f2436;-fx-border-color:rgba(108,142,245,0.35);-fx-border-width:1;-fx-border-radius:12;-fx-background-radius:12;-fx-padding:16 18 16 18;");
        popup.setMaxWidth(300); popup.setMinWidth(240); popup.setMaxHeight(Region.USE_PREF_SIZE);
        StackPane.setAlignment(popup, Pos.CENTER);

        HBox header = new HBox(8);
        header.setAlignment(Pos.CENTER_LEFT);
        Label dateLabel = new Label(monthShort + " " + day);
        dateLabel.setStyle("-fx-font-size:13px;-fx-font-weight:700;-fx-font-family:'Segoe UI';-fx-text-fill:#e8eaf2;");
        HBox sp = new HBox(); HBox.setHgrow(sp, Priority.ALWAYS);
        Button close = new Button("✕");
        close.setStyle("-fx-background-color:rgba(255,255,255,0.06);-fx-text-fill:#9097b4;-fx-font-size:11px;-fx-font-family:'Segoe UI';-fx-border-color:transparent;-fx-cursor:hand;-fx-padding:3 7 3 7;-fx-background-radius:6;");
        close.setOnAction(e -> { contentStack.getChildren().remove(popup); popup = null; });
        header.getChildren().addAll(dateLabel, sp, close);
        popup.getChildren().add(header);

        VBox eventList = new VBox(6);
        popup.getChildren().add(eventList);
        refreshPopupEvents(eventList, day);

        popup.setOnMouseClicked(javafx.event.Event::consume);
        contentStack.getChildren().add(popup);
    }

    private void refreshPopupEvents(VBox eventList, int day) {
        eventList.getChildren().clear();
        List<String[]> events = getEvents(day);
        if (events.isEmpty()) {
            if (popup != null) { contentStack.getChildren().remove(popup); popup = null; }
            return;
        }
        for (String[] evt : events) {
            boolean study = evt[0].toLowerCase().contains("study");
            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(8, 10, 8, 10));
            row.setStyle((study
                    ? "-fx-background-color:rgba(62,207,176,0.1);-fx-border-color:rgba(62,207,176,0.2);"
                    : "-fx-background-color:rgba(245,105,123,0.1);-fx-border-color:rgba(245,105,123,0.2);") +
                    "-fx-border-width:1;-fx-border-radius:8;-fx-background-radius:8;");
            Label timeLbl = new Label(formatTime(evt[1]));
            timeLbl.setMinWidth(62);
            timeLbl.setStyle("-fx-font-size:11px;-fx-font-weight:600;-fx-font-family:'Segoe UI';-fx-text-fill:" + (study ? "#3ecfb0;" : "#f5697b;"));
            Label titleLbl = new Label(evt[0]);
            titleLbl.setStyle("-fx-font-size:12px;-fx-font-family:'Segoe UI';-fx-text-fill:#e8eaf2;");
            HBox.setHgrow(titleLbl, Priority.ALWAYS);
            titleLbl.setMaxWidth(Double.MAX_VALUE);
            titleLbl.setTextOverrun(OverrunStyle.ELLIPSIS);

            final String t0 = evt[0], t1 = evt[1];
            Button delBtn = new Button("✕");
            delBtn.setStyle("-fx-background-color:rgba(245,105,123,0.12);-fx-text-fill:#f5697b;-fx-font-size:10px;-fx-font-family:'Segoe UI';-fx-background-radius:4;-fx-border-color:transparent;-fx-cursor:hand;-fx-padding:2 6 2 6;");
            delBtn.setOnAction(e -> {
                removeEvent(day, t0, t1);
                rebuildGrid();
                refreshPopupEvents(eventList, day);
            });
            row.getChildren().addAll(timeLbl, titleLbl, delBtn);
            eventList.getChildren().add(row);
        }
    }

    private void showAddEventDialog() {
        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color:rgba(0,0,0,0.5);");

        VBox dialog = new VBox(12);
        dialog.setPadding(new Insets(24));
        dialog.setMaxWidth(360); dialog.setMaxHeight(400);
        dialog.setStyle("-fx-background-color:#181c27;-fx-border-color:#ffffff12;-fx-border-width:1;-fx-border-radius:12;-fx-background-radius:12;");

        Label title = new Label("Add Event");
        title.setStyle("-fx-font-size:16px;-fx-font-weight:700;-fx-font-family:'Segoe UI';-fx-text-fill:#e8eaf2;");

        Label dayLbl = new Label("Day (1–" + daysInMonth() + ")");
        dayLbl.setStyle("-fx-font-size:11px;-fx-font-family:'Segoe UI';-fx-text-fill:#9097b4;");
        TextField dayField = new TextField("1");
        dayField.setStyle(fieldStyle());

        Label titleLbl = new Label("Title");
        titleLbl.setStyle("-fx-font-size:11px;-fx-font-family:'Segoe UI';-fx-text-fill:#9097b4;");
        TextField titleField = new TextField();
        titleField.setPromptText("e.g. Math Quiz");
        titleField.setStyle(fieldStyle());

        Label timeLbl = new Label("Time (24h, e.g. 14:30)");
        timeLbl.setStyle("-fx-font-size:11px;-fx-font-family:'Segoe UI';-fx-text-fill:#9097b4;");
        TextField timeField = new TextField();
        timeField.setPromptText("09:00");
        timeField.setStyle(fieldStyle());

        Label errorLbl = new Label("");
        errorLbl.setStyle("-fx-font-size:11px;-fx-font-family:'Segoe UI';-fx-text-fill:#f5697b;");

        HBox buttons = new HBox(8);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        Button cancelBtn = new Button("Cancel");
        cancelBtn.setStyle("-fx-background-color:#1f2436;-fx-text-fill:#9097b4;-fx-font-size:12px;-fx-font-family:'Segoe UI';-fx-background-radius:8;-fx-padding:7 14 7 14;-fx-cursor:hand;-fx-border-color:#ffffff12;-fx-border-width:1;-fx-border-radius:8;");
        cancelBtn.setOnAction(e -> contentStack.getChildren().remove(overlay));
        Button saveBtn = new Button("Save");
        saveBtn.setStyle("-fx-background-color:#6c8ef5;-fx-text-fill:white;-fx-font-size:12px;-fx-font-family:'Segoe UI';-fx-background-radius:8;-fx-padding:7 14 7 14;-fx-cursor:hand;-fx-border-color:transparent;");
        saveBtn.setOnAction(e -> {
            try {
                int day = Integer.parseInt(dayField.getText().trim());
                String t = titleField.getText().trim();
                String time = timeField.getText().trim();
                int dim = daysInMonth();
                if (day < 1 || day > dim) { errorLbl.setText("Day must be 1–" + dim + "."); return; }
                if (t.isEmpty()) { errorLbl.setText("Title required."); return; }
                if (!time.matches("\\d{1,2}:\\d{2}")) { errorLbl.setText("Time must be HH:mm."); return; }
                String[] parts = time.split(":");
                addEvent(day, t, String.format("%02d:%s", Integer.parseInt(parts[0]), parts[1]));
                rebuildGrid();
                contentStack.getChildren().remove(overlay);
            } catch (NumberFormatException ex) { errorLbl.setText("Day must be a number."); }
        });
        buttons.getChildren().addAll(cancelBtn, saveBtn);
        dialog.getChildren().addAll(title, dayLbl, dayField, titleLbl, titleField, timeLbl, timeField, errorLbl, buttons);
        overlay.getChildren().add(dialog);
        StackPane.setAlignment(dialog, Pos.CENTER);
        contentStack.getChildren().add(overlay);
    }

    private void showStudyTimeDialog() {
        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color:rgba(0,0,0,0.5);");

        VBox dialog = new VBox(14);
        dialog.setPadding(new Insets(24));
        dialog.setMaxWidth(420); dialog.setMaxHeight(360);
        dialog.setStyle("-fx-background-color:#181c27;-fx-border-color:#ffffff12;-fx-border-width:1;-fx-border-radius:12;-fx-background-radius:12;");

        Label title = new Label("Schedule Study Time");
        title.setStyle("-fx-font-size:16px;-fx-font-weight:700;-fx-font-family:'Segoe UI';-fx-text-fill:#e8eaf2;");
        Label sub = new Label("Finds empty days 1–2 days before your busiest days and schedules study sessions there.");
        sub.setWrapText(true);
        sub.setStyle("-fx-font-size:12px;-fx-font-family:'Segoe UI';-fx-text-fill:#9097b4;");
        Label countLbl = new Label("How many study sessions?");
        countLbl.setStyle("-fx-font-size:11px;-fx-font-family:'Segoe UI';-fx-text-fill:#9097b4;");
        TextField countField = new TextField("3");
        countField.setStyle(fieldStyle());
        Label resultLbl = new Label("");
        resultLbl.setWrapText(true);
        resultLbl.setStyle("-fx-font-size:12px;-fx-font-family:'Segoe UI';-fx-text-fill:#3ecfb0;");

        HBox buttons = new HBox(8);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        Button cancelBtn = new Button("Cancel");
        cancelBtn.setStyle("-fx-background-color:#1f2436;-fx-text-fill:#9097b4;-fx-font-size:12px;-fx-font-family:'Segoe UI';-fx-background-radius:8;-fx-padding:7 14 7 14;-fx-cursor:hand;-fx-border-color:#ffffff12;-fx-border-width:1;-fx-border-radius:8;");
        cancelBtn.setOnAction(e -> contentStack.getChildren().remove(overlay));
        Button goBtn = new Button("Schedule");
        goBtn.setStyle("-fx-background-color:#3ecfb0;-fx-text-fill:white;-fx-font-size:12px;-fx-font-family:'Segoe UI';-fx-background-radius:8;-fx-padding:7 14 7 14;-fx-cursor:hand;-fx-border-color:transparent;");
        goBtn.setOnAction(e -> {
            try {
                int count = Integer.parseInt(countField.getText().trim());
                if (count < 1 || count > 10) { resultLbl.setStyle("-fx-text-fill:#f5697b;-fx-font-size:12px;-fx-font-family:'Segoe UI';"); resultLbl.setText("Pick 1–10 sessions."); return; }
                List<Integer> scheduled = autoScheduleStudy(count);
                if (scheduled.isEmpty()) {
                    resultLbl.setStyle("-fx-text-fill:#f5697b;-fx-font-size:12px;-fx-font-family:'Segoe UI';");
                    resultLbl.setText("No good days found.");
                } else {
                    resultLbl.setStyle("-fx-text-fill:#3ecfb0;-fx-font-size:12px;-fx-font-family:'Segoe UI';");
                    String mn = LocalDate.of(currentYear, currentMonth, 1).getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
                    StringBuilder sb = new StringBuilder("Scheduled " + scheduled.size() + " session" + (scheduled.size()>1?"s":"") + " on " + mn + " ");
                    for (int i = 0; i < scheduled.size(); i++) { sb.append(scheduled.get(i)); if (i < scheduled.size()-1) sb.append(", "); }
                    resultLbl.setText(sb.append(".").toString());
                    rebuildGrid();
                }
            } catch (NumberFormatException ex) { resultLbl.setStyle("-fx-text-fill:#f5697b;-fx-font-size:12px;-fx-font-family:'Segoe UI';"); resultLbl.setText("Enter a number."); }
        });
        buttons.getChildren().addAll(cancelBtn, goBtn);
        dialog.getChildren().addAll(title, sub, countLbl, countField, resultLbl, buttons);
        overlay.getChildren().add(dialog);
        StackPane.setAlignment(dialog, Pos.CENTER);
        contentStack.getChildren().add(overlay);
    }

    private List<Integer> autoScheduleStudy(int desiredCount) {
        List<Integer> scheduled = new ArrayList<>();
        Set<Integer> busyDays = new TreeSet<>();
        int dim = daysInMonth();
        int sc  = startCol();
        String prefix = currentYear + "-" + currentMonth + "-";
        for (Map.Entry<String, List<String[]>> entry : EVENTS.entrySet()) {
            if (!entry.getKey().startsWith(prefix)) continue;
            try {
                int d = Integer.parseInt(entry.getKey().substring(prefix.length()));
                if (entry.getValue().size() >= 2) busyDays.add(d);
            } catch (NumberFormatException ignored) {}
        }
        for (int busyDay : busyDays) {
            if (scheduled.size() >= desiredCount) break;
            for (int offset = 2; offset >= 1; offset--) {
                int candidate = busyDay - offset;
                if (candidate < 1 || candidate > dim) continue;
                if (currentYear == TODAY_YEAR && currentMonth == TODAY_MONTH && candidate <= TODAY_DAY) continue;
                int col = (sc + candidate - 1) % 7;
                if (col == 0 || col == 6) continue;
                if (getEvents(candidate).isEmpty() && !scheduled.contains(candidate)) {
                    addEvent(candidate, "Study Session", "10:00");
                    scheduled.add(candidate);
                    break;
                }
            }
        }
        if (scheduled.size() < desiredCount) {
            int start = (currentYear == TODAY_YEAR && currentMonth == TODAY_MONTH) ? TODAY_DAY + 1 : 1;
            for (int d = start; d <= dim && scheduled.size() < desiredCount; d++) {
                int col = (sc + d - 1) % 7;
                if (col == 0 || col == 6) continue;
                if (getEvents(d).isEmpty() && !scheduled.contains(d)) {
                    addEvent(d, "Study Session", "14:00");
                    scheduled.add(d);
                }
            }
        }
        Collections.sort(scheduled);
        return scheduled;
    }

    private String fieldStyle() {
        return "-fx-background-color:#1f2436;-fx-text-fill:#e8eaf2;-fx-prompt-text-fill:#5e6482;-fx-border-color:#ffffff20;-fx-border-width:1;-fx-border-radius:8;-fx-background-radius:8;-fx-padding:7 12 7 12;-fx-font-size:13px;-fx-font-family:'Segoe UI';";
    }

    private String formatTime(String t) {
        try {
            String[] p = t.split(":");
            int h = Integer.parseInt(p[0]);
            int h12 = h % 12; if (h12 == 0) h12 = 12;
            return h12 + ":" + p[1] + " " + (h >= 12 ? "PM" : "AM");
        } catch (Exception e) { return t; }
    }

    private Label navItem(String text, boolean active) {
        Label l = new Label(text);
        l.setMaxWidth(Double.MAX_VALUE);
        l.setPadding(new Insets(9, 12, 9, 12));
        l.setStyle(active
                ? "-fx-background-color:rgba(108,142,245,0.12);-fx-text-fill:#6c8ef5;-fx-font-size:13px;-fx-font-family:'Segoe UI';-fx-font-weight:600;-fx-background-radius:8;-fx-border-color:rgba(108,142,245,0.2);-fx-border-width:1;-fx-border-radius:8;"
                : "-fx-background-color:transparent;-fx-text-fill:#9097b4;-fx-font-size:13px;-fx-font-family:'Segoe UI';-fx-background-radius:8;-fx-border-color:transparent;-fx-border-width:1;-fx-border-radius:8;");
        return l;
    }

    private VBox buildCell(int day, boolean isToday, boolean muted, boolean hasStudy) {
        VBox cell = new VBox(4);
        cell.setPadding(new Insets(8));
        String bg = isToday ? "-fx-background-color:rgba(108,142,245,0.06);"
                : hasStudy  ? "-fx-background-color:rgba(62,207,176,0.03);"
                : "-fx-background-color:transparent;";
        cell.setStyle(bg + "-fx-border-color:rgba(255,255,255,0.05);-fx-border-width:0 1 1 0;");
        Label num = new Label(String.valueOf(day));
        num.setStyle("-fx-font-size:12px;-fx-font-family:'Segoe UI';-fx-text-fill:" +
                (muted ? "#5e6482;" : isToday ? "#6c8ef5;-fx-font-weight:500;" : "#e8eaf2;"));
        cell.getChildren().add(num);
        if (isToday) {
            Region bar = new Region();
            bar.setPrefWidth(18); bar.setPrefHeight(3);
            bar.setStyle("-fx-background-color:#6c8ef5;-fx-background-radius:2;");
            cell.getChildren().add(bar);
        }
        return cell;
    }

    private void loadAssignmentsIntoCalendar() {
        if (SessionManager.isDevMode()) return; // dev bypass
        Thread t = new Thread(() -> {
            try {
                FirebaseAuthClient client = new FirebaseAuthClient();
                JsonNode assignments = client.getAssignments(
                        SessionManager.getUserId(), SessionManager.getIdToken());
                for (JsonNode a : assignments) {
                    String dueDate = a.path("dueDate").asText("");
                    String title   = a.path("title").asText("Assignment");
                    if (!dueDate.isBlank()) {
                        try {
                            LocalDate date = LocalDate.parse(dueDate);
                            putEvent(eKey(date.getYear(), date.getMonthValue(),
                                    date.getDayOfMonth()), title, "23:59");
                        } catch (Exception ignored) {}
                    }
                }
                Platform.runLater(this::rebuildGrid);
            } catch (Exception e) { e.printStackTrace(); }
        });
        t.setDaemon(true);
        t.start();
    }

    public Scene getScene() { return scene; }
}
