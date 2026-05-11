package com.classhub.auth;

import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class GradesPage {

    private static final String[][] CLASSES = {
            {"Calculus II",         "92", "A"},
            {"Data Structures",     "90", "A"},
            {"Physics I",           "83", "B"},
            {"English Composition", "88", "A"},
            {"Chemistry Lab",       "79", "B"},
            {"World History",       "85", "A"},
    };

    private Scene scene;

    private Label whatIfLabel;
    private final TextField[] whatIfFields = new TextField[CLASSES.length];

    public GradesPage(Stage stage) {
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
        dashItem.setOnMouseClicked(e -> stage.setScene(new Dashboard(stage).getScene()));
        Label calItem = navItem("Smart Calendar", false);
        calItem.setOnMouseClicked(e -> stage.setScene(new SmartCalendarUI(stage).getScene()));
        Label assignItem = navItem("Assignments", false);
        assignItem.setOnMouseClicked(e -> stage.setScene(new AssignmentsPage(stage).getScene()));
        Label notesItem = navItem("Notes", false);
        notesItem.setOnMouseClicked(e -> stage.setScene(new NotesPage(stage).getScene()));
        nav.getChildren().addAll(dashItem, calItem, assignItem);
        nav.getChildren().add(navItem("Grades", true));
        nav.getChildren().add(notesItem);

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
        logoutBtn.setOnAction(e -> stage.setScene(new LoginPage(stage).getScene()));
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
        Label title = new Label("Grades");
        title.setStyle("-fx-font-size:15px;-fx-font-weight:700;-fx-font-family:'Segoe UI';-fx-text-fill:#e8eaf2;");
        topbar.getChildren().add(title);

        VBox content = new VBox(16);
        content.setPadding(new Insets(20));
        content.setStyle("-fx-background-color:#0f1117;");

        // GPA summary cards
        HBox cards = new HBox(12);
        VBox currentGpa = statCard("Current GPA", "3.8", "#6c8ef5");
        VBox whatIfGpa = new VBox(6);
        whatIfGpa.setPadding(new Insets(14, 16, 14, 16));
        whatIfGpa.setStyle("-fx-background-color:#181c27;-fx-border-color:#ffffff12;-fx-border-width:1;-fx-border-radius:12;-fx-background-radius:12;");
        Label whatIfTitle = new Label("What-If GPA");
        whatIfTitle.setStyle("-fx-font-size:11px;-fx-font-family:'Segoe UI';-fx-text-fill:#5e6482;");
        whatIfLabel = new Label("3.8");
        whatIfLabel.setStyle("-fx-font-size:26px;-fx-font-weight:700;-fx-font-family:'Segoe UI';-fx-text-fill:#3ecfb0;");
        whatIfGpa.getChildren().addAll(whatIfTitle, whatIfLabel);
        VBox avgGrade   = statCard("Avg Grade",   "86%", "#f5a623");
        for (VBox c : new VBox[]{currentGpa, whatIfGpa, avgGrade}) {
            HBox.setHgrow(c, Priority.ALWAYS);
            c.setMaxWidth(Double.MAX_VALUE);
        }
        cards.getChildren().addAll(currentGpa, whatIfGpa, avgGrade);
        content.getChildren().addAll(cards, buildClassSection(), buildWhatIf());

        ScrollPane scroll = new ScrollPane(content);
        scroll.setStyle("-fx-background-color:transparent;-fx-background:#0f1117;");
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setBorder(Border.EMPTY);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        main.getChildren().addAll(topbar, scroll);
        return main;
    }

    private VBox statCard(String label, String value, String color) {
        VBox card = new VBox(6);
        card.setPadding(new Insets(14, 16, 14, 16));
        card.setStyle("-fx-background-color:#181c27;-fx-border-color:#ffffff12;-fx-border-width:1;-fx-border-radius:12;-fx-background-radius:12;");
        Label lbl = new Label(label);
        lbl.setStyle("-fx-font-size:11px;-fx-font-family:'Segoe UI';-fx-text-fill:#5e6482;");
        Label val = new Label(value);
        val.setStyle("-fx-font-size:26px;-fx-font-weight:700;-fx-font-family:'Segoe UI';-fx-text-fill:" + color + ";");
        card.getChildren().addAll(lbl, val);
        return card;
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

    private VBox buildClassSection() {
        VBox section = new VBox(8);
        section.setPadding(new Insets(16, 20, 16, 20));
        section.setStyle("-fx-background-color:#181c27;-fx-border-color:#ffffff12;-fx-border-width:1;-fx-border-radius:12;-fx-background-radius:12;");
        Label title = new Label("Grades by Class");
        title.setStyle("-fx-font-size:13px;-fx-font-weight:700;-fx-font-family:'Segoe UI';-fx-text-fill:#e8eaf2;");
        section.getChildren().add(title);
        for (String[] cls : CLASSES) {
            HBox row = new HBox();
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(10, 14, 10, 14));
            row.setStyle("-fx-background-color:#0f1117;-fx-border-color:#ffffff12;-fx-border-width:1;-fx-border-radius:8;-fx-background-radius:8;");
            Label nameLbl = new Label(cls[0]);
            nameLbl.setStyle("-fx-font-size:13px;-fx-font-family:'Segoe UI';-fx-text-fill:#e8eaf2;");
            HBox.setHgrow(nameLbl, Priority.ALWAYS);
            int g = Integer.parseInt(cls[1]);
            String color = g >= 90 ? "#3ecfb0" : g >= 80 ? "#f5a623" : "#f5697b";
            Label gradeLbl = new Label(cls[2] + "  " + cls[1] + "%");
            gradeLbl.setStyle("-fx-font-size:13px;-fx-font-weight:600;-fx-font-family:'Segoe UI';-fx-text-fill:" + color + ";");
            row.getChildren().addAll(nameLbl, gradeLbl);
            section.getChildren().add(row);
        }
        return section;
    }

    private VBox buildWhatIf() {
        VBox section = new VBox(8);
        section.setPadding(new Insets(16, 20, 16, 20));
        section.setStyle("-fx-background-color:#181c27;-fx-border-color:#ffffff12;-fx-border-width:1;-fx-border-radius:12;-fx-background-radius:12;");
        Label title = new Label("What-If Calculator");
        title.setStyle("-fx-font-size:13px;-fx-font-weight:700;-fx-font-family:'Segoe UI';-fx-text-fill:#e8eaf2;");
        Label subtitle = new Label("Enter hypothetical grades to see how they affect your GPA");
        subtitle.setStyle("-fx-font-size:11px;-fx-font-family:'Segoe UI';-fx-text-fill:#5e6482;");
        section.getChildren().addAll(title, subtitle);

        for (int i = 0; i < CLASSES.length; i++) {
            HBox row = new HBox(12);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(10, 14, 10, 14));
            row.setStyle("-fx-background-color:#0f1117;-fx-border-color:#ffffff12;-fx-border-width:1;-fx-border-radius:8;-fx-background-radius:8;");
            Label name = new Label(CLASSES[i][0]);
            name.setStyle("-fx-font-size:13px;-fx-font-family:'Segoe UI';-fx-text-fill:#e8eaf2;");
            HBox.setHgrow(name, Priority.ALWAYS);
            TextField field = new TextField(CLASSES[i][1]);
            field.setPrefWidth(60); field.setMaxWidth(60);
            field.setStyle("-fx-background-color:#1f2436;-fx-text-fill:#e8eaf2;-fx-font-size:13px;-fx-font-family:'Segoe UI';-fx-border-color:#ffffff20;-fx-border-width:1;-fx-border-radius:6;-fx-background-radius:6;-fx-padding:4 8 4 8;-fx-alignment:center;");
            field.textProperty().addListener((obs, old, nw) -> recalcWhatIf());
            whatIfFields[i] = field;
            row.getChildren().addAll(name, field);
            section.getChildren().add(row);
        }
        return section;
    }

    private void recalcWhatIf() {
        double total = 0; int count = 0;
        for (TextField f : whatIfFields) {
            try {
                double val = Double.parseDouble(f.getText().trim());
                if (val >= 0 && val <= 100) { total += val; count++; }
            } catch (NumberFormatException ignored) {}
        }
        if (count == 0) { whatIfLabel.setText("--"); return; }
        double avg = total / count;
        double gpa = avg >= 93 ? 4.0 : avg >= 90 ? 3.7 : avg >= 87 ? 3.3 :
                avg >= 83 ? 3.0 : avg >= 80 ? 2.7 : avg >= 77 ? 2.3 :
                        avg >= 73 ? 2.0 : avg >= 70 ? 1.7 : 1.0;
        whatIfLabel.setText(String.format("%.1f", gpa));
    }

    public Scene getScene() { return scene; }
}
