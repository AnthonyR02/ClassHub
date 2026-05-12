package com.classhub;

import com.classhub.auth.*;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class ClassHubApplication extends Application {

    public static Scene loginScene, dashboardScene, calendarScene, assignmentsScene, gradesScene, notesScene;

    @Override
    public void start(Stage stage) {
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        double w = bounds.getWidth();
        double h = bounds.getHeight();

        loginScene       = new LoginPage(stage).getScene();
        dashboardScene   = new Dashboard(stage).getScene();
        calendarScene    = new SmartCalendarUI(stage).getScene();
        assignmentsScene = new AssignmentsPage(stage).getScene();
        gradesScene      = new GradesPage(stage).getScene();
        notesScene       = new NotesPage(stage).getScene();

        // size every scene root to fill the screen
        for (Scene s : new Scene[]{loginScene, dashboardScene, calendarScene, assignmentsScene, gradesScene, notesScene}) {
            if (s.getRoot() instanceof Region r) {
                r.setPrefWidth(w);
                r.setPrefHeight(h);
            }
        }

        stage.setScene(loginScene);
        stage.setTitle("ClassHub");
        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());
        stage.setWidth(w);
        stage.setHeight(h);
        stage.setMaximized(true);
        stage.setResizable(true);

        // prevent snap-back to small size when switching scenes
        stage.maximizedProperty().addListener((obs, wasMax, isMax) -> {
            if (!isMax) stage.setMaximized(true);
        });

        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}