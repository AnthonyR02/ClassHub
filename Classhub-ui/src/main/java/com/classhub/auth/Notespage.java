package com.classhub.auth;

import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;

public class NotesPage {

    private Scene scene;

    // Notes Data
    private final ArrayList<String[]> notes = new ArrayList<>();
    private int selectedIndex = -1;

    // UI Components
    private VBox noteList;
    private TextArea editor;
    private TextField titleField;

    public NotesPage(Stage stage) {

        HBox root = new HBox(0);
        root.setStyle("-fx-background-color:#0f1117;");

        root.getChildren().addAll(
                buildSidebar(stage),
                buildNotePanel()
        );

        scene = new Scene(root, 900, 600);
    }

    // Sidebar
    private VBox buildSidebar(Stage stage) {

        VBox sidebar = new VBox(0);

        sidebar.setMinWidth(200);
        sidebar.setMaxWidth(200);

        sidebar.setStyle(
                "-fx-background-color:#181c27;" +
                        "-fx-border-color:#ffffff12;" +
                        "-fx-border-width:0 1 0 0;"
        );

        // Logo
        VBox logo = new VBox(3);

        logo.setPadding(new Insets(20, 16, 16, 16));
        logo.setStyle("-fx-border-color:#ffffff12;-fx-border-width:0 0 1 0;");

        Label logoTitle = new Label("ClassHub");
        logoTitle.setStyle(
                "-fx-font-size:18px;" +
                        "-fx-font-weight:700;" +
                        "-fx-font-family:'Segoe UI';" +
                        "-fx-text-fill:#e8eaf2;"
        );

        Label logoSub = new Label("STUDENT PORTAL");
        logoSub.setStyle(
                "-fx-font-size:9px;" +
                        "-fx-font-family:'Segoe UI';" +
                        "-fx-text-fill:#5e6482;"
        );

        logo.getChildren().addAll(logoTitle, logoSub);

        // Navigation
        VBox nav = new VBox(2);

        nav.setPadding(new Insets(12, 8, 12, 8));
        VBox.setVgrow(nav, Priority.ALWAYS);

        Label section = new Label("MAIN");
        section.setStyle(
                "-fx-font-size:9px;" +
                        "-fx-font-family:'Segoe UI';" +
                        "-fx-text-fill:#5e6482;" +
                        "-fx-padding:10 10 6 10;"
        );

        Label dashItem = navItem("Dashboard", false);
        dashItem.setOnMouseClicked(e ->
                stage.setScene(new Dashboard(stage).getScene()));

        Label calItem = navItem("Smart Calendar", false);
        calItem.setOnMouseClicked(e ->
                stage.setScene(new SmartCalendarUI(stage).getScene()));

        nav.getChildren().addAll(
                section,
                dashItem,
                calItem,
                navItem("Assignments", false),
                navItem("Grades", false),
                navItem("Notes", true)
        );

        // Footer
        VBox footer = new VBox(8);

        footer.setPadding(new Insets(12));
        footer.setStyle("-fx-border-color:#ffffff12;-fx-border-width:1 0 0 0;");

        HBox userRow = new HBox(8);
        userRow.setAlignment(Pos.CENTER_LEFT);

        Label initials = new Label("MF");

        initials.setMinSize(32, 32);
        initials.setMaxSize(32, 32);
        initials.setAlignment(Pos.CENTER);

        initials.setStyle(
                "-fx-background-color:rgba(108,142,245,0.2);" +
                        "-fx-text-fill:#6c8ef5;" +
                        "-fx-font-size:11px;" +
                        "-fx-font-weight:700;" +
                        "-fx-font-family:'Segoe UI';" +
                        "-fx-background-radius:50;"
        );

        VBox userInfo = new VBox(1);

        Label userName = new Label("Myles Freelin");
        userName.setStyle(
                "-fx-font-size:12px;" +
                        "-fx-font-weight:600;" +
                        "-fx-font-family:'Segoe UI';" +
                        "-fx-text-fill:#e8eaf2;"
        );

        Label userRole = new Label("Student");
        userRole.setStyle(
                "-fx-font-size:10px;" +
                        "-fx-font-family:'Segoe UI';" +
                        "-fx-text-fill:#5e6482;"
        );

        userInfo.getChildren().addAll(userName, userRole);
        userRow.getChildren().addAll(initials, userInfo);

        Button logoutBtn = new Button("Logout");

        logoutBtn.setMaxWidth(Double.MAX_VALUE);

        logoutBtn.setStyle(
                "-fx-background-color:rgba(245,105,123,0.1);" +
                        "-fx-text-fill:#f5697b;" +
                        "-fx-font-size:12px;" +
                        "-fx-font-family:'Segoe UI';" +
                        "-fx-background-radius:8;" +
                        "-fx-padding:7 12 7 12;" +
                        "-fx-cursor:hand;" +
                        "-fx-border-color:rgba(245,105,123,0.2);" +
                        "-fx-border-width:1;" +
                        "-fx-border-radius:8;"
        );

        logoutBtn.setOnAction(e ->
                stage.setScene(new LoginPage(stage).getScene()));

        footer.getChildren().addAll(userRow, logoutBtn);

        sidebar.getChildren().addAll(logo, nav, footer);

        return sidebar;
    }

    //  Notes Panel
    private HBox buildNotePanel() {

        HBox panel = new HBox(0);
        HBox.setHgrow(panel, Priority.ALWAYS);

        // Left list column
        VBox listCol = new VBox(0);

        listCol.setMinWidth(220);
        listCol.setMaxWidth(220);

        listCol.setStyle(
                "-fx-background-color:#181c27;" +
                        "-fx-border-color:#ffffff12;" +
                        "-fx-border-width:0 1 0 0;"
        );

        // Header
        HBox listHeader = new HBox(8);

        listHeader.setPadding(new Insets(12));
        listHeader.setAlignment(Pos.CENTER_LEFT);

        listHeader.setStyle(
                "-fx-border-color:#ffffff12;" +
                        "-fx-border-width:0 0 1 0;"
        );

        Label notesLabel = new Label("Notes");

        notesLabel.setStyle(
                "-fx-font-size:14px;" +
                        "-fx-font-weight:700;" +
                        "-fx-font-family:'Segoe UI';" +
                        "-fx-text-fill:#e8eaf2;"
        );

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button newBtn = new Button("+");

        newBtn.setStyle(
                "-fx-background-color:rgba(108,142,245,0.12);" +
                        "-fx-text-fill:#6c8ef5;" +
                        "-fx-font-size:16px;" +
                        "-fx-font-family:'Segoe UI';" +
                        "-fx-background-radius:6;" +
                        "-fx-padding:2 10 2 10;" +
                        "-fx-cursor:hand;" +
                        "-fx-border-color:rgba(108,142,245,0.2);" +
                        "-fx-border-width:1;" +
                        "-fx-border-radius:6;"
        );

        newBtn.setOnAction(e -> newNote());

        listHeader.getChildren().addAll(notesLabel, spacer, newBtn);

        // Note list
        noteList = new VBox(0);

        ScrollPane listScroll = new ScrollPane(noteList);

        listScroll.setFitToWidth(true);

        listScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        listScroll.setStyle(
                "-fx-background:#181c27;" +
                        "-fx-background-color:#181c27;"
        );

        listScroll.setBorder(Border.EMPTY);

        VBox.setVgrow(listScroll, Priority.ALWAYS);

        listCol.getChildren().addAll(listHeader, listScroll);

        // Editor column
        VBox editorCol = new VBox(0);

        HBox.setHgrow(editorCol, Priority.ALWAYS);

        editorCol.setStyle("-fx-background-color:#0f1117;");

        HBox editorHeader = new HBox(10);

        editorHeader.setPadding(new Insets(12, 16, 12, 16));

        editorHeader.setAlignment(Pos.CENTER_LEFT);

        editorHeader.setStyle(
                "-fx-border-color:#ffffff12;" +
                        "-fx-border-width:0 0 1 0;"
        );

        titleField = new TextField();

        titleField.setPromptText("Note title...");

        titleField.setStyle(
                "-fx-background-color:#1f2436;" +
                        "-fx-text-fill:#e8eaf2;" +
                        "-fx-prompt-text-fill:#5e6482;" +
                        "-fx-border-color:#ffffff20;" +
                        "-fx-border-width:1;" +
                        "-fx-border-radius:8;" +
                        "-fx-background-radius:8;" +
                        "-fx-padding:6 12 6 12;" +
                        "-fx-font-size:13px;" +
                        "-fx-font-family:'Segoe UI';"
        );

        HBox.setHgrow(titleField, Priority.ALWAYS);

        Button saveBtn = new Button("Save");

        saveBtn.setStyle(
                "-fx-background-color:#6c8ef5;" +
                        "-fx-text-fill:white;" +
                        "-fx-font-size:13px;" +
                        "-fx-font-family:'Segoe UI';" +
                        "-fx-background-radius:8;" +
                        "-fx-padding:6 16 6 16;" +
                        "-fx-cursor:hand;"
        );

        saveBtn.setOnAction(e -> saveNote());

        Button deleteBtn = new Button("Delete");

        deleteBtn.setStyle(
                "-fx-background-color:rgba(245,105,123,0.1);" +
                        "-fx-text-fill:#f5697b;" +
                        "-fx-font-size:13px;" +
                        "-fx-font-family:'Segoe UI';" +
                        "-fx-background-radius:8;" +
                        "-fx-padding:6 16 6 16;" +
                        "-fx-cursor:hand;" +
                        "-fx-border-color:rgba(245,105,123,0.2);" +
                        "-fx-border-width:1;" +
                        "-fx-border-radius:8;"
        );

        deleteBtn.setOnAction(e -> deleteNote());

        editorHeader.getChildren().addAll(
                titleField,
                saveBtn,
                deleteBtn
        );

        editor = new TextArea();

        editor.setPromptText("Start typing your note...");

        editor.setWrapText(true);

        editor.setStyle(
                "-fx-background-color:#0f1117;" +
                        "-fx-text-fill:#e8eaf2;" +
                        "-fx-prompt-text-fill:#5e6482;" +
                        "-fx-font-size:14px;" +
                        "-fx-font-family:'Segoe UI';" +
                        "-fx-border-color:transparent;" +
                        "-fx-padding:16;"
        );

        VBox.setVgrow(editor, Priority.ALWAYS);

        editorCol.getChildren().addAll(editorHeader, editor);

        panel.getChildren().addAll(listCol, editorCol);

        return panel;
    }

    // Notes Actions
    private void newNote() {

        notes.add(new String[]{"Untitled", ""});

        selectedIndex = notes.size() - 1;

        refreshList();

        titleField.setText("Untitled");
        editor.setText("");

        titleField.requestFocus();
    }

    private void saveNote() {

        if (selectedIndex < 0) {
            return;
        }

        notes.get(selectedIndex)[0] =
                titleField.getText().isBlank()
                        ? "Untitled"
                        : titleField.getText();

        notes.get(selectedIndex)[1] = editor.getText();

        refreshList();
    }

    private void deleteNote() {

        if (selectedIndex < 0) {
            return;
        }

        notes.remove(selectedIndex);

        selectedIndex = notes.isEmpty()
                ? -1
                : Math.max(0, selectedIndex - 1);

        refreshList();

        if (selectedIndex >= 0) {
            loadNote(selectedIndex);
        } else {
            titleField.clear();
            editor.clear();
        }
    }

    private void loadNote(int index) {

        selectedIndex = index;

        titleField.setText(notes.get(index)[0]);
        editor.setText(notes.get(index)[1]);

        refreshList();
    }

    private void refreshList() {

        noteList.getChildren().clear();

        for (int i = 0; i < notes.size(); i++) {

            final int index = i;

            VBox item = new VBox(3);

            item.setPadding(new Insets(10, 14, 10, 14));

            item.setStyle(
                    (i == selectedIndex
                            ? "-fx-background-color:rgba(108,142,245,0.1);" +
                            "-fx-border-color:#6c8ef5;" +
                            "-fx-border-width:0 0 0 2;"
                            : "-fx-background-color:transparent;" +
                            "-fx-border-color:#ffffff08;" +
                            "-fx-border-width:0 0 1 0;")
                            + "-fx-cursor:hand;"
            );

            Label title = new Label(notes.get(i)[0]);

            title.setStyle(
                    "-fx-font-size:13px;" +
                            "-fx-font-weight:600;" +
                            "-fx-font-family:'Segoe UI';" +
                            "-fx-text-fill:#e8eaf2;"
            );

            String content = notes.get(i)[1];

            Label preview = new Label(
                    content.isBlank()
                            ? "No content"
                            : content.length() > 40
                            ? content.substring(0, 40) + "..."
                            : content
            );

            preview.setStyle(
                    "-fx-font-size:11px;" +
                            "-fx-font-family:'Segoe UI';" +
                            "-fx-text-fill:#5e6482;"
            );

            item.getChildren().addAll(title, preview);

            item.setOnMouseClicked(e -> loadNote(index));

            noteList.getChildren().add(item);
        }
    }

    // Helper
    private Label navItem(String text, boolean active) {

        Label l = new Label(text);

        l.setMaxWidth(Double.MAX_VALUE);

        l.setPadding(new Insets(9, 12, 9, 12));

        l.setStyle(
                active
                        ? "-fx-background-color:rgba(108,142,245,0.12);" +
                        "-fx-text-fill:#6c8ef5;" +
                        "-fx-font-size:13px;" +
                        "-fx-font-family:'Segoe UI';" +
                        "-fx-font-weight:600;" +
                        "-fx-background-radius:8;" +
                        "-fx-border-color:rgba(108,142,245,0.2);" +
                        "-fx-border-width:1;" +
                        "-fx-border-radius:8;"
                        : "-fx-background-color:transparent;" +
                        "-fx-text-fill:#9097b4;" +
                        "-fx-font-size:13px;" +
                        "-fx-font-family:'Segoe UI';"
        );

        return l;
    }

    public Scene getScene() {
        return scene;
    }
}