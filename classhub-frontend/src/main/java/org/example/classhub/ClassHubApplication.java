package org.example.classhub;

import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class ClassHubApplication extends Application {

    public static Scene loginScene, dashboardScene, calendarScene, assignmentsScene, gradesScene, notesScene, flashcardsScene;

    // Called after login succeeds — builds all scenes with a valid session
    public static void buildAppScenes(Stage stage) {
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        double w = bounds.getWidth();
        double h = bounds.getHeight();

        dashboardScene   = new Dashboard(stage).getScene();
        calendarScene    = new SmartCalendarUI(stage).getScene();
        assignmentsScene = new AssignmentsPage(stage).getScene();
        gradesScene      = new GradesPage(stage).getScene();
        notesScene       = new NotesPage(stage).getScene();
        flashcardsScene  = new FlashcardsPage(stage).getScene();

        for (Scene s : new Scene[]{dashboardScene, calendarScene, assignmentsScene, gradesScene, notesScene, flashcardsScene}) {
            if (s.getRoot() instanceof Region r) {
                r.setPrefWidth(w);
                r.setPrefHeight(h);
            }
        }
    }

    @Override
    public void start(Stage stage) {
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        double w = bounds.getWidth();
        double h = bounds.getHeight();

        // Only build the login scene at startup — no session yet
        loginScene = new LoginPage(stage).getScene();
        if (loginScene.getRoot() instanceof Region r) {
            r.setPrefWidth(w);
            r.setPrefHeight(h);
        }

        stage.setScene(loginScene);
        stage.setTitle("ClassHub");
        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());
        stage.setWidth(w);
        stage.setHeight(h);
        stage.setMaximized(true);
        stage.setResizable(true);

        stage.maximizedProperty().addListener((obs, wasMax, isMax) -> {
            if (!isMax) stage.setMaximized(true);
        });

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
