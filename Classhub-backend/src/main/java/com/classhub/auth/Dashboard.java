package com.classhub.auth;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Dashboard {

    private Scene scene;

    public Dashboard(Stage stage) {

        Label welcome = new Label("Welcome to ClassHub 🎓");
        welcome.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label info = new Label("You are now logged in.");

        Button logoutBtn = new Button("Logout");

        // 🔁 Go back to login
        logoutBtn.setOnAction(e -> {
            stage.setScene(new LoginPage(stage).getScene());
        });

        VBox layout = new VBox(15);
        layout.getChildren().addAll(welcome, info, logoutBtn);
        layout.setAlignment(Pos.CENTER);

        scene = new Scene(layout, 400, 300);
    }

    public Scene getScene() {
        return scene;
    }
}