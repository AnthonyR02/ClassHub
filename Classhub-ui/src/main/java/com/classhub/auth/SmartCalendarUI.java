package com.classhub.auth;

import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class SmartCalendarUI {

    private Scene scene;

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
        nav.getChildren().add(navItem("Assignments", false));
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

        ScrollPane scroll = new ScrollPane(content);
        scroll.setStyle("-fx-background-color:transparent;-fx-background:#0f1117;");
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setBorder(Border.EMPTY);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        main.getChildren().addAll(topbar, scroll);
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

    public Scene getScene() { return scene; }
}
