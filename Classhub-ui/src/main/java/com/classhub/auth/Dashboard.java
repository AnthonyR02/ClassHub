package com.classhub.auth;

import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.layout.*;

public class Dashboard {

    private static final String BG      = "#0f1117";
    private static final String SURFACE = "#181c27";
    private static final String BORDER  = "#ffffff12";
    private static final String TEXT    = "#e8eaf2";
    private static final String TEXT2   = "#9097b4";
    private static final String TEXT3   = "#5e6482";
    private static final String ACCENT  = "#6c8ef5";
    private static final String ROSE    = "#f5697b";
    private static final String SURFACE2= "#1f2436";
    private static final String[][] CLASSES = {
            {"Calculus II",         "MWF 9:00 AM"},
            {"Data Structures",     "TTH 10:30 AM"},
            {"Physics I",           "MWF 11:00 AM"},
            {"English Composition", "TTH 1:00 PM"},
            {"Chemistry Lab",       "W 2:00 PM"},
            {"World History",       "MWF 2:00 PM"},
    };

    private Scene scene;

    public Dashboard(Stage stage) {

        HBox root = new HBox(0);
        root.setStyle("-fx-background-color:" + BG + ";");
        root.getChildren().addAll(buildSidebar(stage), buildMain());
        scene = new Scene(root, 900, 600);
    }

    private VBox buildSidebar(Stage stage) {
        VBox sidebar = new VBox(0);
        sidebar.setMinWidth(200);
        sidebar.setMaxWidth(200);
        sidebar.setStyle(
                "-fx-background-color:" + SURFACE + ";" +
                        "-fx-border-color:" + BORDER + ";-fx-border-width:0 1 0 0;"
        );

        // Logo
        VBox logo = new VBox(3);
        logo.setPadding(new Insets(20, 16, 16, 16));
        logo.setStyle("-fx-border-color:" + BORDER + ";-fx-border-width:0 0 1 0;");
        Label logoTitle = new Label("ClassHub");
        logoTitle.setStyle("-fx-font-size:18px;-fx-font-weight:700;-fx-font-family:'Segoe UI';-fx-text-fill:" + TEXT + ";");
        Label logoSub = new Label("STUDENT PORTAL");
        logoSub.setStyle("-fx-font-size:9px;-fx-font-family:'Segoe UI';-fx-text-fill:" + TEXT3 + ";");
        logo.getChildren().addAll(logoTitle, logoSub);

        // Nav
        VBox nav = new VBox(2);
        nav.setPadding(new Insets(12, 8, 12, 8));
        VBox.setVgrow(nav, Priority.ALWAYS);
        nav.getChildren().add(navSection("MAIN"));
        nav.getChildren().add(navItem("Dashboard",      true));
        Label calItem = navItem("Smart Calendar", false);
        calItem.setOnMouseClicked(e -> stage.setScene(new SmartCalendarUI(stage).getScene()));
        nav.getChildren().add(calItem);
        Label assignItem = navItem("Assignments", false);
        assignItem.setOnMouseClicked(e -> stage.setScene(new AssignmentsPage(stage).getScene()));
        nav.getChildren().add(assignItem);
        nav.getChildren().add(navItem("Grades",         false));
        nav.getChildren().add(navItem("Notes",          false));

        // placeholder footer
        VBox footer = new VBox(8);
        footer.setPadding(new Insets(12));
        footer.setStyle("-fx-border-color:" + BORDER + ";-fx-border-width:1 0 0 0;");

        HBox userRow = new HBox(8);
        userRow.setAlignment(Pos.CENTER_LEFT);
        Label initials = new Label("MF");
        initials.setMinWidth(32); initials.setMinHeight(32);
        initials.setMaxWidth(32); initials.setMaxHeight(32);
        initials.setAlignment(Pos.CENTER);
        initials.setStyle(
                "-fx-background-color:rgba(108,142,245,0.2);-fx-text-fill:" + ACCENT + ";" +
                        "-fx-font-size:11px;-fx-font-weight:700;-fx-font-family:'Segoe UI';-fx-background-radius:50;"
        );
        VBox userInfo = new VBox(1);
        Label userName = new Label("Myles Freelin");
        userName.setStyle("-fx-font-size:12px;-fx-font-weight:600;-fx-font-family:'Segoe UI';-fx-text-fill:" + TEXT + ";");
        Label userRole = new Label("Student");
        userRole.setStyle("-fx-font-size:10px;-fx-font-family:'Segoe UI';-fx-text-fill:" + TEXT3 + ";");
        userInfo.getChildren().addAll(userName, userRole);
        userRow.getChildren().addAll(initials, userInfo);

        Button settingsBtn = new Button("Settings");
        settingsBtn.setMaxWidth(Double.MAX_VALUE);
        settingsBtn.setStyle(
                "-fx-background-color:" + SURFACE2 + ";-fx-text-fill:" + TEXT2 + ";" +
                        "-fx-font-size:12px;-fx-font-family:'Segoe UI';-fx-background-radius:8;" +
                        "-fx-padding:7 12 7 12;-fx-cursor:hand;" +
                        "-fx-border-color:" + BORDER + ";-fx-border-width:1;-fx-border-radius:8;"
        );

        Button logoutBtn = new Button("Logout");
        logoutBtn.setMaxWidth(Double.MAX_VALUE);
        logoutBtn.setStyle(
                "-fx-background-color:rgba(245,105,123,0.1);-fx-text-fill:" + ROSE + ";" +
                        "-fx-font-size:12px;-fx-font-family:'Segoe UI';-fx-background-radius:8;" +
                        "-fx-padding:7 12 7 12;-fx-cursor:hand;" +
                        "-fx-border-color:rgba(245,105,123,0.2);-fx-border-width:1;-fx-border-radius:8;"
        );
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
        topbar.setStyle(
                "-fx-background-color:" + BG + ";" +
                        "-fx-border-color:" + BORDER + ";-fx-border-width:0 0 1 0;"
        );
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

        main.getChildren().addAll(topbar, scroll);

        content.getChildren().addAll(buildStatRow(), buildClassesSection());

        return main;
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
                ? "-fx-background-color:rgba(108,142,245,0.12);-fx-text-fill:" + ACCENT + ";" +
                "-fx-font-size:13px;-fx-font-family:'Segoe UI';-fx-font-weight:600;" +
                "-fx-background-radius:8;-fx-border-color:rgba(108,142,245,0.2);-fx-border-width:1;-fx-border-radius:8;"
                : "-fx-background-color:transparent;-fx-text-fill:" + TEXT2 + ";" +
                "-fx-font-size:13px;-fx-font-family:'Segoe UI';" +
                "-fx-background-radius:8;-fx-border-color:transparent;-fx-border-width:1;-fx-border-radius:8;"
        );
        return l;
    }

    private HBox buildStatRow() {
        HBox row = new HBox(12);
        VBox c1 = statCard("GPA",           "3.8", "#6c8ef5");
        VBox c2 = statCard("Pending Tasks", "6",   "#f5697b");
        VBox c3 = statCard("Avg Grade",     "86%", "#3ecfb0");
        VBox c4 = statCard("Attendance",    "91%", "#f5a623");
        for (VBox c : new VBox[]{c1, c2, c3, c4}) {
            HBox.setHgrow(c, Priority.ALWAYS);
            c.setMaxWidth(Double.MAX_VALUE);
        }
        row.getChildren().addAll(c1, c2, c3, c4);
        return row;
    }

    private VBox statCard(String label, String value, String color) {
        VBox card = new VBox(6);
        card.setPadding(new Insets(14, 16, 14, 16));
        card.setStyle(
                "-fx-background-color:" + SURFACE + ";" +
                        "-fx-border-color:" + BORDER + ";" +
                        "-fx-border-width:1;-fx-border-radius:12;-fx-background-radius:12;"
        );
        Label lbl = new Label(label);
        lbl.setStyle("-fx-font-size:11px;-fx-font-family:'Segoe UI';-fx-text-fill:" + TEXT3 + ";");
        Label val = new Label(value);
        val.setStyle("-fx-font-size:26px;-fx-font-weight:700;-fx-font-family:'Segoe UI';-fx-text-fill:" + color + ";");
        card.getChildren().addAll(lbl, val);
        return card;
    }

    private VBox buildClassesSection() {
        VBox outerCard = new VBox(12);
        outerCard.setPadding(new Insets(18, 20, 18, 20));
        outerCard.setStyle(
                "-fx-background-color:" + SURFACE + ";" +
                        "-fx-border-color:" + BORDER + ";" +
                        "-fx-border-width:1;-fx-border-radius:12;-fx-background-radius:12;"
        );
        Label title = new Label("My Classes");
        title.setStyle("-fx-font-size:13px;-fx-font-weight:700;-fx-font-family:'Segoe UI';-fx-text-fill:" + TEXT + ";");

        int cols = 2;
        int rows = (int) Math.ceil(CLASSES.length / (double) cols);
        VBox grid = new VBox(10);
        for (int r = 0; r < rows; r++) {
            HBox row = new HBox(10);
            for (int c = 0; c < cols; c++) {
                int idx = r * cols + c;
                if (idx < CLASSES.length) {
                    VBox card = classCard(CLASSES[idx][0], CLASSES[idx][1]);
                    HBox.setHgrow(card, Priority.ALWAYS);
                    card.setMaxWidth(Double.MAX_VALUE);
                    row.getChildren().add(card);
                } else {
                    Region filler = new Region();
                    HBox.setHgrow(filler, Priority.ALWAYS);
                    row.getChildren().add(filler);
                }
            }
            grid.getChildren().add(row);
        }
        outerCard.getChildren().addAll(title, grid);
        return outerCard;
    }

    private VBox classCard(String name, String time) {
        VBox card = new VBox(6);
        card.setPadding(new Insets(0, 20, 0, 20));
        card.setMinHeight(80); card.setMaxHeight(80);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle(
                "-fx-background-color:" + BG + ";" +
                        "-fx-border-color:" + BORDER + ";" +
                        "-fx-border-width:1;-fx-border-radius:8;-fx-background-radius:8;-fx-cursor:hand;"
        );
        Label nameLbl = new Label(name);
        nameLbl.setStyle("-fx-font-size:14px;-fx-font-weight:700;-fx-font-family:'Segoe UI';-fx-text-fill:" + TEXT + ";");
        Label timeLbl = new Label(time);
        timeLbl.setStyle("-fx-font-size:12px;-fx-font-family:'Segoe UI';-fx-text-fill:" + TEXT3 + ";");
        card.getChildren().addAll(nameLbl, timeLbl);
        return card;
    }

    public Scene getScene() {
        return scene;
    }
}