package com.classhub.auth;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Dashboard {

    public void show(Stage stage) {
        Label label = new Label("Welcome to ClassHub Dashboard");

        VBox layout = new VBox(label);

        stage.setScene(new Scene(layout, 500, 400));
    }
}