package org.example.classhub;


import com.fasterxml.jackson.databind.JsonNode;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Border;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;

public class AssignmentsPage {

    private Scene scene;

    private static final String[] FILTER_OPTIONS = {"All", "Completed", "In Progress", "Overdue"};
    private String activeFilter = "All";
    private VBox itemList;
    private HBox filterRow;

    private static final String STATUS_COMPLETED   = "#3ecfb0";
    private static final String STATUS_OVERDUE     = "#f5697b";
    private static final String STATUS_IN_PROGRESS = "#f5a623";

    private enum AssignmentStatus {
        COMPLETED, OVERDUE, IN_PROGRESS
    }

    private final ArrayList<String[]> ITEMS = new ArrayList<>();

    public AssignmentsPage(Stage stage) {
        HBox root = new HBox(0);
        root.setStyle("-fx-background-color:#0f1117;");
        root.getChildren().addAll(buildSidebar(stage), buildMain());
        scene = new Scene(root, 900, 600);
        loadAssignments();
    }

    private void loadAssignments() {
        Thread thread = new Thread(() -> {
            try {
                FirebaseAuthClient client = new FirebaseAuthClient();
                JsonNode assignments = client.getAssignments(
                        SessionManager.getUserId(),
                        SessionManager.getIdToken());

                ITEMS.clear();

                for (JsonNode a : assignments) {
                    ITEMS.add(new String[]{
                            "ASSIGNMENT",
                            a.path("title").asText(),
                            a.path("courseId").asText(),   // will show courseId for now
                            "Due " + a.path("dueDate").asText(),
                            String.valueOf(a.path("completed").asBoolean()),
                            a.path("id").asText()});           // store id at index 5

                }
                Platform.runLater(this::refreshList);

            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    itemList.getChildren().clear();
                    Label err = new Label("Could not load assignments.");
                    err.setStyle("-fx-font-size:13px;-fx-font-family:'Segoe UI';-fx-text-fill:#f5697b;");
                    itemList.getChildren().add(err);
                });
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    private AssignmentStatus getStatus(String[] item) {
        boolean completed = item[4].equals("true");
        if (completed)
            return AssignmentStatus.COMPLETED;

        String dueDate = item[3];
        if (dueDate != null && !dueDate.isBlank()) {
            try {
                if (LocalDate.parse(dueDate).isBefore(LocalDate.now())) {
                    return AssignmentStatus.OVERDUE;
                }
            } catch (Exception ignored) {}
        }
        return AssignmentStatus.IN_PROGRESS;
    }

    private String statusColor(AssignmentStatus status) {
        return switch (status) {
            case COMPLETED   -> STATUS_COMPLETED;
            case OVERDUE     -> STATUS_OVERDUE;
            case IN_PROGRESS -> STATUS_IN_PROGRESS;
        };
    }

    private String statusLabel(AssignmentStatus status) {
        return switch (status) {
            case COMPLETED   -> "COMPLETED";
            case OVERDUE     -> "OVERDUE";
            case IN_PROGRESS -> "IN PROGRESS";
        };
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
        for (String cls : FILTER_OPTIONS) filterRow.getChildren().add(buildFilterChip(cls));
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
        for (String cls : FILTER_OPTIONS) filterRow.getChildren().add(buildFilterChip(cls));
    }

    private void refreshList() {
        itemList.getChildren().clear();
        for (int i = 0; i < ITEMS.size(); i++) {
            String[] item = ITEMS.get(i);

            AssignmentStatus status = getStatus(item);
            if (!activeFilter.equals("All")) {
                boolean matches = switch (activeFilter) {
                    case "Completed"   -> status == AssignmentStatus.COMPLETED;
                    case "In Progress" -> status == AssignmentStatus.IN_PROGRESS;
                    case "Overdue"     -> status == AssignmentStatus.OVERDUE;
                    default            -> true;
                };
                if (!matches) continue;
            }
            itemList.getChildren().add(buildItem(i, item, status));
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

    private HBox buildItem(int idx, String[] item, AssignmentStatus status) {
        boolean isAssignment = item[0].equals("ASSIGNMENT");
        boolean isComplete = status == AssignmentStatus.COMPLETED;
        String color = statusColor(status);

        HBox row = new HBox(12);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(12, 16, 12, 16));
        row.setStyle("-fx-background-color:#181c27;-fx-border-color:#ffffff12;-fx-border-width:1;-fx-border-radius:10;-fx-background-radius:10;");

        Label statusBadge = new Label(statusLabel(status));
        statusBadge.setStyle(
                "-fx-background-color:" + color + "22;" +
                        "-fx-text-fill:" + color + ";" +
                        "-fx-font-size:10px;-fx-font-weight:700;-fx-font-family:'Segoe UI';" +
                        "-fx-background-radius:4;-fx-padding:2 8 2 8;"
        );

        Label checkbox = null;
        if (isAssignment) {
            checkbox = new Label(isComplete ? "✓" : "");
            checkbox.setMinWidth(18);
            checkbox.setMinHeight(18);
            checkbox.setMaxWidth(18);
            checkbox.setMaxHeight(18);
            checkbox.setAlignment(Pos.CENTER);
            checkbox.setStyle(isComplete
                    ? "-fx-background-color:#3ecfb0;-fx-text-fill:white;-fx-font-size:12px;-fx-font-weight:700;-fx-background-radius:4;-fx-cursor:hand;"
                    : "-fx-background-color:transparent;-fx-border-color:#5e6482;-fx-border-width:1.5;-fx-border-radius:4;-fx-cursor:hand;"
            );
            checkbox.setOnMouseClicked(e -> {
                String assignmentId = item[5]; // id stored at index 5
                Thread t = new Thread(() -> {
                    try {
                        FirebaseAuthClient client = new FirebaseAuthClient();
                        client.markAssignmentComplete(assignmentId, SessionManager.getIdToken());
                        ITEMS.get(idx)[4] = isComplete ? "false" : "true";
                        refreshList();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });
                t.setDaemon(true);
                t.start();
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

        row.getChildren().addAll(checkbox, statusBadge, info);
        return row;
    }

    public Scene getScene() { return scene; }
}