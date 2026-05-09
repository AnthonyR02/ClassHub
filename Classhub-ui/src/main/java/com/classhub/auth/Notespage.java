package com.classhub.auth;

import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class NotesPage {

    private Scene scene;

    public NotesPage(Stage stage) {
        HBox root = new HBox(0);
        root.setStyle("-fx-background-color:#0f1117;");

        root.getChildren().addAll(
                buildSidebar(stage),
                buildNotePanel()
        );

        scene = new Scene(root, 900, 600);
    }

    // ── Sidebar ───────────────────────────────────────────────
    private VBox buildSidebar(Stage stage) {

        VBox sidebar = new VBox(0);
        sidebar.setMinWidth(200);
        sidebar.setMaxWidth(200);

        sidebar.setStyle(
                "-fx-background-color:#181c27;" +
                        "-fx-border-color:#ffffff12;" +
                        "-fx-border-width:0 1 0 0;"
        );

        // Logo section
        VBox logo = new VBox(3);
        logo.setPadding(new Insets(20, 16, 16, 16));
        logo.setStyle("-fx-border-color:#ffffff12;-fx-border-width:0 0 1 0;");

        Label logoTitle = new Label("ClassHub");
        logoTitle.setStyle("-fx-font-size:18px;-fx-font-weight:700;-fx-text-fill:#e8eaf2;");

        Label logoSub = new Label("STUDENT PORTAL");
        logoSub.setStyle("-fx-font-size:9px;-fx-text-fill:#5e6482;");

        logo.getChildren().addAll(logoTitle, logoSub);

        // Navigation
        VBox nav = new VBox(2);
        nav.setPadding(new Insets(12, 8, 12, 8));
        VBox.setVgrow(nav, Priority.ALWAYS);

        Label section = new Label("MAIN");
        section.setStyle("-fx-font-size:9px;-fx-text-fill:#5e6482;-fx-padding:10 10 6 10;");

        Label dashItem = navItem("Dashboard", false);
        dashItem.setOnMouseClicked(e -> stage.setScene(new Dashboard(stage).getScene()));

        Label calItem = navItem("Smart Calendar", false);
        calItem.setOnMouseClicked(e -> stage.setScene(new SmartCalendarUI(stage).getScene()));

        Label notesItem = navItem("Notes", true);

        nav.getChildren().addAll(
                section,
                dashItem,
                calItem,
                navItem("Assignments", false),
                navItem("Grades", false),
                notesItem
        );

        // Footer
        VBox footer = new VBox(8);
        footer.setPadding(new Insets(12));
        footer.setStyle("-fx-border-color:#ffffff12;-fx-border-width:1 0 0 0;");

        HBox userRow = new HBox(8);
        userRow.setAlignment(Pos.CENTER_LEFT);

        Label initials = new Label("MF");
        initials.setMinSize(32, 32);
        initials.setAlignment(Pos.CENTER);
        initials.setStyle(
                "-fx-background-color:rgba(108,142,245,0.2);" +
                        "-fx-text-fill:#6c8ef5;" +
                        "-fx-font-size:11px;" +
                        "-fx-font-weight:700;" +
                        "-fx-background-radius:50;"
        );

        VBox userInfo = new VBox(1);
        Label userName = new Label("Myles Freelin");
        userName.setStyle("-fx-text-fill:#e8eaf2;-fx-font-size:12px;-fx-font-weight:600;");

        Label userRole = new Label("Student");
        userRole.setStyle("-fx-text-fill:#5e6482;-fx-font-size:10px;");

        userInfo.getChildren().addAll(userName, userRole);
        userRow.getChildren().addAll(initials, userInfo);

        Button logoutBtn = new Button("Logout");
        logoutBtn.setMaxWidth(Double.MAX_VALUE);
        logoutBtn.setStyle(
                "-fx-background-color:rgba(245,105,123,0.1);" +
                        "-fx-text-fill:#f5697b;" +
                        "-fx-border-color:rgba(245,105,123,0.2);" +
                        "-fx-background-radius:8;" +
                        "-fx-border-radius:8;"
        );

        logoutBtn.setOnAction(e -> stage.setScene(new LoginPage(stage).getScene()));

        footer.getChildren().addAll(userRow, logoutBtn);

        sidebar.getChildren().addAll(logo, nav, footer);

        return sidebar;
    }

    // ── Notes Panel (EMPTY SHELL ONLY) ───────────────────────
    private HBox buildNotePanel() {

        HBox panel = new HBox(0);
        HBox.setHgrow(panel, Priority.ALWAYS);

        // Left list column (UI only)
        VBox listCol = new VBox(0);
        listCol.setMinWidth(220);
        listCol.setMaxWidth(220);

        listCol.setStyle(
                "-fx-background-color:#181c27;" +
                        "-fx-border-color:#ffffff12;" +
                        "-fx-border-width:0 1 0 0;"
        );

        Label notesLabel = new Label("Notes");
        notesLabel.setPadding(new Insets(12));
        notesLabel.setStyle("-fx-text-fill:#e8eaf2;-fx-font-size:14px;-fx-font-weight:700;");

        ScrollPane listScroll = new ScrollPane();
        listScroll.setFitToWidth(true);
        listScroll.setStyle("-fx-background:#181c27;");

        VBox listPlaceholder = new VBox();
        listScroll.setContent(listPlaceholder);

        VBox.setVgrow(listScroll, Priority.ALWAYS);

        listCol.getChildren().addAll(notesLabel, listScroll);

        // Right editor column (UI only)
        VBox editorCol = new VBox(0);
        VBox.setVgrow(editorCol, Priority.ALWAYS);

        editorCol.setStyle("-fx-background-color:#0f1117;");

        HBox editorHeader = new HBox(10);
        editorHeader.setPadding(new Insets(12));
        editorHeader.setStyle("-fx-border-color:#ffffff12;-fx-border-width:0 0 1 0;");

        TextField titleField = new TextField();
        titleField.setPromptText("Note title...");
        HBox.setHgrow(titleField, Priority.ALWAYS);

        Button saveBtn = new Button("Save");
        Button deleteBtn = new Button("Delete");

        editorHeader.getChildren().addAll(titleField, saveBtn, deleteBtn);

        TextArea editor = new TextArea();
        editor.setPromptText("Start typing your note...");
        VBox.setVgrow(editor, Priority.ALWAYS);

        editorCol.getChildren().addAll(editorHeader, editor);

        panel.getChildren().addAll(listCol, editorCol);

        return panel;
    }

    // ── Helper ───────────────────────────────────────────────
    private Label navItem(String text, boolean active) {
        Label l = new Label(text);
        l.setMaxWidth(Double.MAX_VALUE);
        l.setPadding(new Insets(9, 12, 9, 12));

        l.setStyle(active
                ? "-fx-text-fill:#6c8ef5;-fx-background-color:rgba(108,142,245,0.12);-fx-background-radius:8;"
                : "-fx-text-fill:#9097b4;"
        );

        return l;
    }

    public Scene getScene() {
        return scene;
    }
}