package com.classhub;

import com.classhub.auth.LoginPage;
import javafx.application.Application;
import javafx.stage.Stage;

public class ClassHubApplication extends Application {

    @Override
    public void start(Stage stage) {
        stage.setScene(new LoginPage(stage).getScene());
        stage.setTitle("ClassHub");
        stage.show();

    }
}