package com.classhub.auth;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginPage {

    private Scene scene;

    public LoginPage(Stage stage) {

        Label title = new Label("Login");

        TextField username = new TextField();
        username.setPromptText("Username");

        PasswordField password = new PasswordField();
        password.setPromptText("Password");

        Button loginBtn = new Button("Login");
        Button registerBtn = new Button("Register");

        // login logic
        loginBtn.setOnAction(e -> {
            String user = username.getText();
            String pass = password.getText();

            if (user.equals("admin") && pass.equals("1234")) {
                stage.setScene(new Dashboard(stage).getScene());
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Invalid login");
                alert.show();
            }
        });

        // go to register page
        registerBtn.setOnAction(e -> {
            stage.setScene(new RegisterPage(stage).getScene());
        });

        VBox layout = new VBox(12);

        username.setMaxWidth(200);
        password.setMaxWidth(200);
        loginBtn.setMaxWidth(120);
        registerBtn.setMaxWidth(120);

        layout.getChildren().addAll(title, username, password, loginBtn, registerBtn);
        layout.setAlignment(Pos.CENTER);

        scene = new Scene(layout, 300, 200);
    }

    public Scene getScene() {
        return scene;
    }
}