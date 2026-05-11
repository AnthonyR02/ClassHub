package com.classhub.auth;

import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class SmartCalendarUI {

    private Scene scene;

    private static final int TODAY         = 15;
    private static final int START_DAY     = 3;
    private static final int DAYS_IN_MONTH = 30;
    private static final int[] PREV_DAYS   = {29, 30, 31};
    private static final int[] NEXT_DAYS   = {1, 2};
    private VBox popup;

    private static final java.util.Map<Integer, java.util.List<String>> EVENTS = new java.util.HashMap<>();
    static {
        EVENTS.put(2, java.util.Arrays.asList("CS HW"));
        EVENTS.put(3, java.util.Arrays.asList("CS HW", "CS HW"));
        EVENTS.put(5, java.util.Arrays.asList("CS HW", "CS HW", "CS HW"));
        EVENTS.put(8, java.util.Arrays.asList("CS HW"));
        EVENTS.put(10, java.util.Arrays.asList("CS HW", "CS HW"));
        EVENTS.put(22, java.util.Arrays.asList("CS HW", "CS HW"));
        EVENTS.put(30, java.util.Arrays.asList("CS HW"));
    }

    public SmartCalendarUI(Stage stage) {
        HBox root = new HBox(0);
        root.setStyle("-fx-background-color:#0f1117;");
        root.getChildren().addAll(buildSidebar(stage), buildMain());
        scene = new Scene(root, 900, 600);
    }

    private VBox buildSidebar(Stage stage) {
        VBox sidebar = new VBox(0);
        sidebar.setMinWidth(200); sidebar.setMaxWidth(200);
        sidebar.setStyle("-fx-background-color:#181c27;-fx-border-color:#ffffff12;-fx-border-width:0 1 0 0;");

        // Logo
        VBox logo = new VBox(3);
        logo.setPadding(new Insets(20, 16, 16, 16));
        logo.setStyle("-fx-border-color:#ffffff12;-fx-border-width:0 0 1 0;");
        Label logoTitle = new Label("ClassHub");
        logoTitle.setStyle("-fx-font-size:18px;-fx-font-weight:700;-fx-font-family:'Segoe UI';-fx-text-fill:#e8eaf2;");
        Label logoSub = new Label("STUDENT PORTAL");
        logoSub.setStyle("-fx-font-size:9px;-fx-font-family:'Segoe UI';-fx-text-fill:#5e6482;");
        logo.getChildren().addAll(logoTitle, logoSub);

        // Nav
        VBox nav = new VBox(2);
        nav.setPadding(new Insets(12, 8, 12, 8));
        VBox.setVgrow(nav, Priority.ALWAYS);
        Label section = new Label("MAIN");
        section.setStyle("-fx-font-size:9px;-fx-font-family:'Segoe UI';-fx-text-fill:#5e6482;-fx-padding:10 10 6 10;");
        nav.getChildren().add(section);

        Label dashItem = navItem("Dashboard", false);
        dashItem.setOnMouseClicked(e -> stage.setScene(new Dashboard(stage).getScene()));
        nav.getChildren().add(dashItem);
        nav.getChildren().add(navItem("Smart Calendar", true));
        Label assignItem = navItem("Assignments", false);
        assignItem.setOnMouseClicked(e -> stage.setScene(new AssignmentsPage(stage).getScene()));
        nav.getChildren().add(assignItem);
        nav.getChildren().add(navItem("Grades", false));
        nav.getChildren().add(navItem("Notes", false));

        //footer
        VBox footer = new VBox(8);
        footer.setMinHeight(60);
        footer.setStyle("-fx-border-color:#ffffff12;-fx-border-width:1 0 0 0;");

        HBox userRow = new HBox(8);
        userRow.setAlignment(Pos.CENTER_LEFT);
        Label initials = new Label("MF");
        initials.setMinWidth(32); initials.setMinHeight(32);
        initials.setMaxWidth(32); initials.setMaxHeight(32);
        initials.setAlignment(Pos.CENTER);
        initials.setStyle("-fx-background-color:rgba(108,142,245,0.2);-fx-text-fill:#6c8ef5;-fx-font-size:11px;-fx-font-weight:700;-fx-font-family:'Segoe UI';-fx-background-radius:50;");

        VBox userInfo = new VBox(1);
        Label userName = new Label("Myles Freelin");
        userName.setStyle("-fx-font-size:12px;-fx-font-weight:600;-fx-font-family:'Segoe UI';-fx-text-fill:#e8eaf2;");
        Label userRole = new Label("Student");
        userRole.setStyle("-fx-font-size:10px;-fx-font-family:'Segoe UI';-fx-text-fill:#5e6482;");
        userInfo.getChildren().addAll(userName, userRole);
        userRow.getChildren().addAll(initials, userInfo);

        Button settingsBtn = new Button("Settings");
        settingsBtn.setMaxWidth(Double.MAX_VALUE);
        settingsBtn.setStyle("-fx-background-color:#1f2436;-fx-text-fill:#9097b4;-fx-font-size:12px;-fx-font-family:'Segoe UI';-fx-background-radius:8;-fx-padding:7 12 7 12;-fx-cursor:hand;-fx-border-color:#ffffff12;-fx-border-width:1;-fx-border-radius:8;");

        Button logoutBtn = new Button("Logout");
        logoutBtn.setMaxWidth(Double.MAX_VALUE);
        logoutBtn.setStyle("-fx-background-color:rgba(245,105,123,0.1);-fx-text-fill:#f5697b;-fx-font-size:12px;-fx-font-family:'Segoe UI';-fx-background-radius:8;-fx-padding:7 12 7 12;-fx-cursor:hand;-fx-border-color:rgba(245,105,123,0.2);-fx-border-width:1;-fx-border-radius:8;");
        logoutBtn.setOnAction(e -> stage.setScene(new LoginPage(stage).getScene()));

        footer.getChildren().addAll(userRow, settingsBtn, logoutBtn);
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

        HBox monthRow = new HBox(12);
        monthRow.setAlignment(Pos.CENTER_LEFT);

        Button prevBtn = new Button("←");
        prevBtn.setStyle("-fx-background-color:#1f2436;-fx-text-fill:#9097b4;-fx-font-size:13px;-fx-font-family:'Segoe UI';-fx-background-radius:8;-fx-padding:6 10 6 10;-fx-cursor:hand;-fx-border-color:#ffffff12;-fx-border-width:1;-fx-border-radius:8;");

        Label monthLabel = new Label("April 2026");
        monthLabel.setStyle("-fx-font-size:15px;-fx-font-weight:500;-fx-font-family:'Segoe UI';-fx-text-fill:#e8eaf2;");

        Button nextBtn = new Button("→");
        nextBtn.setStyle("-fx-background-color:#1f2436;-fx-text-fill:#9097b4;-fx-font-size:13px;-fx-font-family:'Segoe UI';-fx-background-radius:8;-fx-padding:6 10 6 10;-fx-cursor:hand;-fx-border-color:#ffffff12;-fx-border-width:1;-fx-border-radius:8;");

        Button todayBtn = new Button("Today");
        todayBtn.setStyle("-fx-background-color:rgba(108,142,245,0.12);-fx-text-fill:#6c8ef5;-fx-font-size:12px;-fx-font-family:'Segoe UI';-fx-background-radius:8;-fx-padding:7 14 7 14;-fx-cursor:hand;-fx-border-color:rgba(108,142,245,0.2);-fx-border-width:1;-fx-border-radius:8;");

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        monthRow.getChildren().addAll(prevBtn, monthLabel, nextBtn, spacer, todayBtn);

        content.getChildren().add(monthRow);

        // declare contentStack here so the grid loop can reference it
        StackPane contentStack = new StackPane();
        VBox.setVgrow(contentStack, Priority.ALWAYS);
        contentStack.setOnMouseClicked(e -> { if (popup != null) popup.setVisible(false); });

        // calendar outer card
        VBox calOuter = new VBox(0);
        calOuter.setStyle("-fx-background-color:#181c27;-fx-border-color:#ffffff12;-fx-border-width:1;-fx-border-radius:12;-fx-background-radius:12;");
        VBox.setVgrow(calOuter, Priority.ALWAYS);

// day headers
        HBox dayHeaders = new HBox(0);
        String[] days = {"Sun","Mon","Tue","Wed","Thu","Fri","Sat"};
        for (String d : days) {
            Label lbl = new Label(d);
            lbl.setMaxWidth(Double.MAX_VALUE);
            lbl.setAlignment(Pos.CENTER);
            lbl.setStyle("-fx-font-size:11px;-fx-font-weight:500;-fx-font-family:'Segoe UI';-fx-text-fill:#5e6482;-fx-border-color:#ffffff12;-fx-border-width:0 0 1 0;-fx-padding:10 0 10 0;");
            HBox.setHgrow(lbl, Priority.ALWAYS);
            dayHeaders.getChildren().add(lbl);
        }
        calOuter.getChildren().add(dayHeaders);

// grid
        GridPane grid = new GridPane();
        grid.setMaxWidth(Double.MAX_VALUE);
        grid.setMaxHeight(Double.MAX_VALUE);
        VBox.setVgrow(grid, Priority.ALWAYS);

        for (int c = 0; c < 7; c++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(100.0 / 7);
            grid.getColumnConstraints().add(cc);
        }
        for (int r = 0; r < 5; r++) {
            RowConstraints rc = new RowConstraints();
            rc.setMinHeight(72);
            grid.getRowConstraints().add(rc);
        }

        int col = 0, row = 0;
        for (int pd : PREV_DAYS) {
            grid.add(buildCell(pd, false, true), col++, row);
        }
            for (int d = 1; d <= DAYS_IN_MONTH; d++) {
                VBox cell = buildCell(d, d == TODAY, false);
                java.util.List<String> evts = EVENTS.getOrDefault(d, java.util.Collections.emptyList());

                if (evts.size() == 1) {
                    Label pill = new Label(evts.get(0));
                    pill.setMaxWidth(Double.MAX_VALUE);
                    pill.setStyle("-fx-background-color:rgba(245,105,123,0.15);-fx-text-fill:#f5697b;-fx-font-size:10px;-fx-font-family:'Segoe UI';-fx-background-radius:4;-fx-padding:2 6 2 6;");
                    cell.getChildren().add(pill);
                } else if (evts.size() > 1) {
                    Label pill = new Label(evts.size() + " events");
                    pill.setMaxWidth(Double.MAX_VALUE);
                    pill.setStyle("-fx-background-color:rgba(108,142,245,0.15);-fx-text-fill:#6c8ef5;-fx-font-size:10px;-fx-font-family:'Segoe UI';-fx-background-radius:4;-fx-padding:2 6 2 6;");
                    cell.getChildren().add(pill);
                }
                if (!evts.isEmpty()) {
                    final int day = d;
                    final java.util.List<String> events = evts;
                    cell.setOnMouseClicked(e -> showPopup(contentStack, "April " + day, events));
                }
                grid.add(cell, col, row);
                col++;
                if (col == 7) { col = 0; row++; }
        }
        for (int nd : NEXT_DAYS) {
            grid.add(buildCell(nd, false, true), col++, row);
        }
        calOuter.getChildren().add(grid);
        content.getChildren().add(calOuter);

            ScrollPane scroll = new ScrollPane(content);
            scroll.setStyle("-fx-background-color:transparent;-fx-background:#0f1117;");
            scroll.setFitToWidth(true);
            scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            scroll.setBorder(Border.EMPTY);
            VBox.setVgrow(scroll, Priority.ALWAYS);

        contentStack.getChildren().add(scroll);

            main.getChildren().addAll(topbar, contentStack);
            return main;
    }

    private Label navItem(String text, boolean active) {
        Label l = new Label(text);
        l.setMaxWidth(Double.MAX_VALUE);
        l.setPadding(new Insets(9, 12, 9, 12));
        l.setStyle(active
                ? "-fx-background-color:rgba(108,142,245,0.12);-fx-text-fill:#6c8ef5;-fx-font-size:13px;-fx-font-family:'Segoe UI';-fx-font-weight:600;-fx-background-radius:8;-fx-border-color:rgba(108,142,245,0.2);-fx-border-width:1;-fx-border-radius:8;"
                : "-fx-background-color:transparent;-fx-text-fill:#9097b4;-fx-font-size:13px;-fx-font-family:'Segoe UI';-fx-background-radius:8;-fx-border-color:transparent;-fx-border-width:1;-fx-border-radius:8;"
        );
        return l;
    }
    private VBox buildCell(int day, boolean isToday, boolean muted) {
        VBox cell = new VBox(4);
        cell.setPadding(new Insets(8));
        cell.setStyle(
                (isToday ? "-fx-background-color:rgba(108,142,245,0.06);" : "-fx-background-color:transparent;") +
                        "-fx-border-color:rgba(255,255,255,0.05);-fx-border-width:0 1 1 0;"
        );

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
        private void showPopup(StackPane stack, String dateLabel, java.util.List<String> events) {
            if (popup != null) stack.getChildren().remove(popup);

            popup = new VBox(8);
            popup.setStyle("-fx-background-color:#1f2436;-fx-border-color:rgba(108,142,245,0.3);-fx-border-width:1;-fx-border-radius:10;-fx-background-radius:10;-fx-padding:14 16 14 16;");
            popup.setMaxWidth(220);
            StackPane.setAlignment(popup, Pos.CENTER);

            HBox header = new HBox();
            header.setAlignment(Pos.CENTER_LEFT);
            Label date = new Label(dateLabel);
            date.setStyle("-fx-font-size:12px;-fx-font-weight:500;-fx-font-family:'Segoe UI';-fx-text-fill:#9097b4;");
            HBox sp = new HBox(); HBox.setHgrow(sp, Priority.ALWAYS);
            Button close = new Button("close");
            close.setStyle("-fx-background-color:transparent;-fx-text-fill:#5e6482;-fx-font-size:12px;-fx-font-family:'Segoe UI';-fx-border-color:transparent;-fx-cursor:hand;-fx-padding:0;");
            close.setOnAction(e -> popup.setVisible(false));
            header.getChildren().addAll(date, sp, close);
            popup.getChildren().add(header);

            for (String evt : events) {
                Label item = new Label(evt);
                item.setMaxWidth(Double.MAX_VALUE);
                item.setStyle("-fx-background-color:rgba(245,105,123,0.15);-fx-text-fill:#f5697b;-fx-font-size:12px;-fx-font-family:'Segoe UI';-fx-background-radius:6;-fx-padding:5 10 5 10;");
                popup.getChildren().add(item);
            }
            stack.getChildren().add(popup);
        }

    public Scene getScene() { return scene; }
}
