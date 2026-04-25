package com.classhub.auth;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginPage {

    private Scene scene;

    public LoginPage(Stage stage) {

        Label title = new Label("ClassHub");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Button loginBtn = new Button("Login");
        Hyperlink createAccountLink = new Hyperlink("Create Account");

        // error label
        Label errorLabel = new Label("");
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);

        // login logic
        loginBtn.setOnAction(e -> {
            String email = emailField.getText().trim();
            String pass  = passwordField.getText().trim();

            if (email.isEmpty() || pass.isEmpty()) {
                errorLabel.setText("Please fill in all fields.");
                errorLabel.setVisible(true);
                errorLabel.setManaged(true);
                return;
            }

            // show success
            errorLabel.setVisible(false);
            errorLabel.setManaged(false);
            stage.setScene(new Dashboard(stage).getScene());
        });

        // go to register page
        createAccountLink.setOnAction(e -> {
            stage.setScene(new RegisterPage(stage).getScene());
        });

        VBox layout = new VBox(12);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(40));

        emailField.setMaxWidth(200);
        passwordField.setMaxWidth(200);
        loginBtn.setMaxWidth(200);

        layout.getChildren().addAll(title, emailField, passwordField, loginBtn, createAccountLink, errorLabel);
        layout.setAlignment(Pos.CENTER);

        scene = new Scene(layout, 900, 600);
    }

    public Scene getScene() {
        return scene;
    }
}