package com.classhub.auth;

import com.classhub.ClassHubApplication;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.*;

public class AssignmentsPage {

    private Scene scene;

    private static final String[] CLASSES = {"All", "Calculus II", "Data Structures", "Physics I", "English Composition", "Chemistry Lab", "World History"};
    private String activeFilter = "All";
    private VBox itemList;
    private HBox filterRow;

    private static final ArrayList<String[]> ITEMS = new ArrayList<>(Arrays.asList(
            new String[]{"ASSIGNMENT",   "CS HW: Recursion practice",         "Data Structures",     "Due Apr 12",   "false"},
            new String[]{"ANNOUNCEMENT", "Midterm review session Friday 3pm", "Calculus II",         "Posted Apr 8", "false"},
            new String[]{"ASSIGNMENT",   "Lab Report 3",                      "Chemistry Lab",       "Due Apr 3",    "true"},
            new String[]{"ASSIGNMENT",   "Essay: Industrial Revolution",      "English Composition", "Due Apr 18",   "false"},
            new String[]{"ANNOUNCEMENT", "Class cancelled this Wednesday",    "Physics I",           "Posted Apr 7", "false"},
            new String[]{"ASSIGNMENT",   "Problem set 7",                     "Physics I",           "Due Apr 14",   "false"},
            new String[]{"ASSIGNMENT",   "Reading: Chapters 5-6",             "World History",       "Due Apr 16",   "false"},
            new String[]{"ANNOUNCEMENT", "Office hours moved to Tuesday",     "Data Structures",     "Posted Apr 5", "false"},
            new String[]{"ASSIGNMENT",   "Quiz prep worksheet",               "Calculus II",         "Due Apr 11",   "true"},
            new String[]{"ASSIGNMENT",   "Group project proposal",            "World History",       "Due Apr 20",   "false"}
    ));

    public AssignmentsPage(Stage stage) {
        HBox root = new HBox(0);
        root.setStyle("-fx-background-color:#0f1117;");
        root.getChildren().addAll(buildSidebar(stage), buildMain());
        scene = new Scene(root, 900, 600);
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
        Label calItem = navItem("Smart Calendar", false);
        calItem.setOnMouseClicked(e -> stage.setScene(ClassHubApplication.calendarScene));
        Label gradesItem = navItem("Grades", false);
        gradesItem.setOnMouseClicked(e -> stage.setScene(ClassHubApplication.gradesScene));
        Label notesItem = navItem("Notes", false);
        notesItem.setOnMouseClicked(e -> stage.setScene(ClassHubApplication.notesScene));
        nav.getChildren().addAll(dashItem, calItem);
        nav.getChildren().add(navItem("Assignments", true));
        nav.getChildren().addAll(gradesItem, notesItem);

        VBox footer = new VBox(8);
        footer.setPadding(new Insets(12));
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
        Button logoutBtn = new Button("Logout");
        logoutBtn.setMaxWidth(Double.MAX_VALUE);
        logoutBtn.setStyle("-fx-background-color:rgba(245,105,123,0.1);-fx-text-fill:#f5697b;-fx-font-size:12px;-fx-font-family:'Segoe UI';-fx-background-radius:8;-fx-padding:7 12 7 12;-fx-cursor:hand;-fx-border-color:rgba(245,105,123,0.2);-fx-border-width:1;-fx-border-radius:8;");
        logoutBtn.setOnAction(e -> stage.setScene(ClassHubApplication.loginScene));
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
        Label title = new Label("Assignments & Announcements");
        title.setStyle("-fx-font-size:15px;-fx-font-weight:700;-fx-font-family:'Segoe UI';-fx-text-fill:#e8eaf2;");
        topbar.getChildren().add(title);

        VBox content = new VBox(14);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color:#0f1117;");
        VBox.setVgrow(content, Priority.ALWAYS);

        HBox filterContainer = new HBox(10);
        filterContainer.setAlignment(Pos.CENTER_LEFT);
        Label filterLabel = new Label("Filter:");
        filterLabel.setStyle("-fx-font-size:12px;-fx-font-family:'Segoe UI';-fx-text-fill:#5e6482;");
        filterRow = new HBox(6);
        for (String cls : CLASSES) filterRow.getChildren().add(buildFilterChip(cls));
        filterContainer.getChildren().addAll(filterLabel, filterRow);

        itemList = new VBox(8);
        refreshList();

        ScrollPane scroll = new ScrollPane(itemList);
        scroll.setStyle("-fx-background-color:transparent;-fx-background:#0f1117;");
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setBorder(Border.EMPTY);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        content.getChildren().addAll(filterContainer, scroll);
        main.getChildren().addAll(topbar, content);
        return main;
    }

    private Label buildFilterChip(String cls) {
        Label chip = new Label(cls);
        chip.setPadding(new Insets(5, 12, 5, 12));
        boolean active = cls.equals(activeFilter);
        chip.setStyle(active
                ? "-fx-background-color:rgba(108,142,245,0.12);-fx-text-fill:#6c8ef5;-fx-font-size:12px;-fx-font-family:'Segoe UI';-fx-background-radius:6;-fx-border-color:rgba(108,142,245,0.2);-fx-border-width:1;-fx-border-radius:6;-fx-cursor:hand;"
                : "-fx-background-color:#181c27;-fx-text-fill:#9097b4;-fx-font-size:12px;-fx-font-family:'Segoe UI';-fx-background-radius:6;-fx-border-color:#ffffff12;-fx-border-width:1;-fx-border-radius:6;-fx-cursor:hand;"
        );
        chip.setOnMouseClicked(e -> {
            activeFilter = cls;
            refreshFilterChips();
            refreshList();
        });
        return chip;
    }

    private void refreshFilterChips() {
        filterRow.getChildren().clear();
        for (String cls : CLASSES) filterRow.getChildren().add(buildFilterChip(cls));
    }

    private void refreshList() {
        itemList.getChildren().clear();
        for (int i = 0; i < ITEMS.size(); i++) {
            String[] item = ITEMS.get(i);
            if (!activeFilter.equals("All") && !item[2].equals(activeFilter)) continue;
            itemList.getChildren().add(buildItem(i, item));
        }
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

    private HBox buildItem(int idx, String[] item) {
        boolean isAssignment = item[0].equals("ASSIGNMENT");
        boolean isComplete = item[4].equals("true");

        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(12, 16, 12, 16));
        row.setStyle("-fx-background-color:#181c27;-fx-border-color:#ffffff12;-fx-border-width:1;-fx-border-radius:10;-fx-background-radius:10;");

        if (isAssignment) {
            Label checkbox = new Label(isComplete ? "✓" : "");
            checkbox.setMinWidth(18); checkbox.setMinHeight(18);
            checkbox.setMaxWidth(18); checkbox.setMaxHeight(18);
            checkbox.setAlignment(Pos.CENTER);
            checkbox.setStyle(isComplete
                    ? "-fx-background-color:#3ecfb0;-fx-text-fill:white;-fx-font-size:12px;-fx-font-weight:700;-fx-background-radius:4;-fx-cursor:hand;"
                    : "-fx-background-color:transparent;-fx-border-color:#5e6482;-fx-border-width:1.5;-fx-border-radius:4;-fx-cursor:hand;"
            );
            checkbox.setOnMouseClicked(e -> {
                ITEMS.get(idx)[4] = isComplete ? "false" : "true";
                refreshList();
            });
            row.getChildren().add(checkbox);
        }

        Label badge = new Label(item[0]);
        badge.setStyle(isAssignment
                ? "-fx-background-color:rgba(108,142,245,0.15);-fx-text-fill:#6c8ef5;-fx-font-size:10px;-fx-font-weight:700;-fx-font-family:'Segoe UI';-fx-background-radius:4;-fx-padding:2 8 2 8;"
                : "-fx-background-color:rgba(159,127,254,0.15);-fx-text-fill:#9f7ffe;-fx-font-size:10px;-fx-font-weight:700;-fx-font-family:'Segoe UI';-fx-background-radius:4;-fx-padding:2 8 2 8;"
        );

        VBox info = new VBox(2);
        HBox.setHgrow(info, Priority.ALWAYS);
        Label titleLbl = new Label(item[1]);
        titleLbl.setStyle(isComplete
                ? "-fx-font-size:13px;-fx-font-weight:600;-fx-font-family:'Segoe UI';-fx-text-fill:#5e6482;-fx-strikethrough:true;"
                : "-fx-font-size:13px;-fx-font-weight:600;-fx-font-family:'Segoe UI';-fx-text-fill:#e8eaf2;"
        );
        Label sub = new Label(item[2] + " · " + item[3]);
        sub.setStyle("-fx-font-size:11px;-fx-font-family:'Segoe UI';-fx-text-fill:" + (isComplete ? "#5e6482" : "#9097b4") + ";");
        info.getChildren().addAll(titleLbl, sub);

        row.getChildren().addAll(badge, info);
        return row;
    }

    public Scene getScene() { return scene; }
}