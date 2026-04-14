package com.classhub.views;

import com.classhub.utils.StyleHelper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class MainWindow extends HBox {

    private VBox contentArea;
    private Label[] navButtons;
    private Label pageTitle;

    private static final String[][] NAV = {
        {"🏠", "Dashboard"},
        {"📅", "Schedule"},
        {"📝", "Assignments"},
        {"⭐", "Grades"},
        {"📓", "Notes"},
        {"✅", "Attendance"},
        {"📢", "Announcements"},
    };

    public MainWindow() {
        setBackground(StyleHelper.bg(StyleHelper.BG));
        buildSidebar();
        buildMain();
    }

    private void buildSidebar() {
        VBox sidebar = new VBox(0);
        sidebar.setMinWidth(220); sidebar.setMaxWidth(220);
        sidebar.setBackground(StyleHelper.bg(StyleHelper.SURFACE));
        sidebar.setBorder(new Border(new BorderStroke(
                Color.web(StyleHelper.BORDER), BorderStrokeStyle.SOLID,
                CornerRadii.EMPTY, new BorderWidths(0, 1, 0, 0))));

        // Logo
        VBox logoBox = new VBox(4);
        logoBox.setPadding(new Insets(22, 20, 18, 20));
        logoBox.setBorder(new Border(new BorderStroke(
                Color.web(StyleHelper.BORDER), BorderStrokeStyle.SOLID,
                CornerRadii.EMPTY, new BorderWidths(0, 0, 1, 0))));
        Label logoTitle = StyleHelper.labelBold("ClassHub", 20, StyleHelper.TEXT);
        Label logoSub   = StyleHelper.label("STUDENT PORTAL", 9, StyleHelper.TEXT3);
        logoBox.getChildren().addAll(logoTitle, logoSub);

        // Nav
        VBox nav = new VBox(3);
        nav.setPadding(new Insets(12, 10, 12, 10));
        VBox.setVgrow(nav, Priority.ALWAYS);

        Label mainSection = StyleHelper.label("MAIN", 9, StyleHelper.TEXT3);
        mainSection.setPadding(new Insets(10, 10, 6, 10));
        nav.getChildren().add(mainSection);

        navButtons = new Label[NAV.length];
        for (int i = 0; i < NAV.length; i++) {
            final int idx = i;
            Label btn = new Label(NAV[i][0] + "  " + NAV[i][1]);
            btn.setMaxWidth(Double.MAX_VALUE);
            btn.setPadding(new Insets(9, 12, 9, 12));
            btn.setFont(javafx.scene.text.Font.font("System", 13));
            styleNavBtn(btn, i == 0);
            btn.setOnMouseClicked(e -> switchView(idx));
            btn.setCursor(javafx.scene.Cursor.HAND);
            navButtons[i] = btn;
            nav.getChildren().add(btn);

            if (i == 3) {
                Label tools = StyleHelper.label("TOOLS", 9, StyleHelper.TEXT3);
                tools.setPadding(new Insets(14, 10, 6, 10));
                nav.getChildren().add(tools);
            }
        }

        // User footer
        HBox footer = new HBox(10);
        footer.setPadding(new Insets(14, 16, 14, 16));
        footer.setAlignment(Pos.CENTER_LEFT);
        footer.setBorder(new Border(new BorderStroke(
                Color.web(StyleHelper.BORDER), BorderStrokeStyle.SOLID,
                CornerRadii.EMPTY, new BorderWidths(1, 0, 0, 0))));

        Label av = new Label("MF");
        av.setBackground(StyleHelper.bgRadius(StyleHelper.ACCENT + "33", 50));
        av.setTextFill(Color.web(StyleHelper.ACCENT));
        av.setFont(javafx.scene.text.Font.font("System", javafx.scene.text.FontWeight.BOLD, 11));
        av.setMinWidth(34); av.setMinHeight(34);
        av.setMaxWidth(34); av.setMaxHeight(34);
        av.setAlignment(Pos.CENTER);

        VBox userInfo = new VBox(2);
        userInfo.getChildren().addAll(
            StyleHelper.labelBold("Myles Freelin", 13, StyleHelper.TEXT),
            StyleHelper.label("College Student · Junior", 11, StyleHelper.TEXT3)
        );
        footer.getChildren().addAll(av, userInfo);

        sidebar.getChildren().addAll(logoBox, nav, footer);
        getChildren().add(sidebar);
    }

    private void buildMain() {
        VBox mainArea = new VBox(0);
        HBox.setHgrow(mainArea, Priority.ALWAYS);

        // Topbar
        HBox topbar = new HBox(14);
        topbar.setPrefHeight(56); topbar.setMinHeight(56);
        topbar.setAlignment(Pos.CENTER_LEFT);
        topbar.setPadding(new Insets(0, 24, 0, 24));
        topbar.setBackground(StyleHelper.bg(StyleHelper.BG));
        topbar.setBorder(new Border(new BorderStroke(
                Color.web(StyleHelper.BORDER), BorderStrokeStyle.SOLID,
                CornerRadii.EMPTY, new BorderWidths(0, 0, 1, 0))));

        pageTitle = StyleHelper.labelBold("Dashboard", 16, StyleHelper.TEXT);
        HBox sp = new HBox(); HBox.setHgrow(sp, Priority.ALWAYS);

        TextField searchField = new TextField();
        searchField.setPromptText("Search…");
        searchField.setBackground(StyleHelper.bgRadius(StyleHelper.SURFACE2, 8));
        searchField.setStyle("-fx-text-fill:" + StyleHelper.TEXT + ";" +
                             "-fx-prompt-text-fill:" + StyleHelper.TEXT3 + ";" +
                             "-fx-border-color:" + StyleHelper.BORDER2 + ";" +
                             "-fx-border-radius:8;-fx-background-radius:8;" +
                             "-fx-padding:7 12 7 12;-fx-font-size:13;");
        searchField.setPrefWidth(200);

        Label settingsBtn = new Label("⚙  Settings");
        settingsBtn.setBackground(StyleHelper.bgRadius(StyleHelper.SURFACE2, 8));
        settingsBtn.setTextFill(Color.web(StyleHelper.TEXT));
        settingsBtn.setBorder(new Border(new BorderStroke(
                Color.web(StyleHelper.BORDER2), BorderStrokeStyle.SOLID,
                new CornerRadii(8), new BorderWidths(1))));
        settingsBtn.setPadding(new Insets(7, 14, 7, 14));
        settingsBtn.setFont(javafx.scene.text.Font.font("System", 13));

        topbar.getChildren().addAll(pageTitle, sp, searchField, settingsBtn);

        // Scrollable content
        ScrollPane scroll = new ScrollPane();
        scroll.setStyle("-fx-background-color:transparent;-fx-background:" + StyleHelper.BG + ";");
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setBorder(Border.EMPTY);
        VBox.setVgrow(scroll, Priority.ALWAYS);

        contentArea = new VBox();
        contentArea.setPadding(new Insets(26));
        contentArea.setBackground(StyleHelper.bg(StyleHelper.BG));
        scroll.setContent(contentArea);

        mainArea.getChildren().addAll(topbar, scroll);
        getChildren().add(mainArea);

        switchView(0);
    }

    private void switchView(int idx) {
        for (int i = 0; i < navButtons.length; i++)
            styleNavBtn(navButtons[i], i == idx);

        pageTitle.setText(NAV[idx][1]);
        contentArea.getChildren().clear();

        Node view = switch (idx) {
            case 0 -> new DashboardView();
            case 1 -> new ScheduleView();
            case 2 -> new AssignmentsView();
            case 3 -> new GradesView();
            case 4 -> new NotesView();
            case 5 -> new AttendanceView();
            case 6 -> new AnnouncementsView();
            default -> new DashboardView();
        };
        contentArea.getChildren().add(view);
    }

    private void styleNavBtn(Label btn, boolean active) {
        if (active) {
            btn.setBackground(StyleHelper.bgRadius(StyleHelper.ACCENT + "1e", 8));
            btn.setTextFill(Color.web(StyleHelper.ACCENT));
            btn.setBorder(new Border(new BorderStroke(
                    Color.web(StyleHelper.ACCENT + "33"), BorderStrokeStyle.SOLID,
                    new CornerRadii(8), new BorderWidths(1))));
            btn.setFont(javafx.scene.text.Font.font("System", javafx.scene.text.FontWeight.SEMI_BOLD, 13));
        } else {
            btn.setBackground(StyleHelper.bgRadius("transparent", 8));
            btn.setTextFill(Color.web(StyleHelper.TEXT2));
            btn.setBorder(new Border(new BorderStroke(
                    Color.TRANSPARENT, BorderStrokeStyle.SOLID,
                    new CornerRadii(8), new BorderWidths(1))));
            btn.setFont(javafx.scene.text.Font.font("System", 13));
        }
    }
}
