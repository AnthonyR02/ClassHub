package com.classhub;

import com.classhub.auth.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ClassHubApplication extends Application {

    public static Scene loginScene, dashboardScene, calendarScene, assignmentsScene, gradesScene, notesScene;

    @Override
    public void start(Stage stage) {
        loginScene       = new LoginPage(stage).getScene();
        dashboardScene   = new Dashboard(stage).getScene();
        calendarScene    = new SmartCalendarUI(stage).getScene();
        assignmentsScene = new AssignmentsPage(stage).getScene();
        gradesScene      = new GradesPage(stage).getScene();
        notesScene       = new NotesPage(stage).getScene();

        stage.setScene(loginScene);
        stage.setTitle("ClassHub");
        stage.show();
        stage.setMaximized(true);
        stage.setResizable(true);

        // make sure screen stays maximized
        stage.maximizedProperty().addListener((obs, wasMax, isMax) -> {
            if (!isMax) stage.setMaximized(true);
        });

        stage.show();
    }

    public static void main(String[] args) { launch(args); }
}