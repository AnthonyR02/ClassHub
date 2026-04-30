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
        nav.getChildren().add(navItem("Smart Calendar", false));
        nav.getChildren().add(navItem("Assignments",    false));
        nav.getChildren().add(navItem("Grades",         false));
        nav.getChildren().add(navItem("Notes",          false));

        // placeholder footer
        VBox footer = new VBox();
        footer.setMinHeight(60);
        footer.setStyle("-fx-border-color:" + BORDER + ";-fx-border-width:1 0 0 0;");

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

    public Scene getScene() {
        return scene;
    }
}