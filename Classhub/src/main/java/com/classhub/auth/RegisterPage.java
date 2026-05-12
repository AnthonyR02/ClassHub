package com.classhub.auth;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class RegisterPage {

    private Scene scene;

    public RegisterPage(Stage stage) {

        Label title = new Label("Register");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TextField username = new TextField();
        username.setPromptText("Username");

        PasswordField password = new PasswordField();
        password.setPromptText("Password");

        Button registerBtn = new Button("Register");
        Button backBtn = new Button("Back");

        //Back to Login
        backBtn.setOnAction(e -> {
            stage.setScene(com.classhub.ClassHubApplication.loginScene);
        });

        VBox root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(20));

        root.getChildren().addAll(title, username, password, registerBtn, backBtn);

        scene = new Scene(root, 400, 300);
    }

    public Scene getScene() {
        return scene;
    }
}