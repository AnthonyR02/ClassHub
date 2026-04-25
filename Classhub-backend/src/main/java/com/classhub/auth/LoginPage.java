package com.classhub.auth;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginPage {

    private static final String BG      = "#0f1117";
    private static final String SURFACE = "#181c27";
    private static final String BORDER  = "#ffffff12";
    private static final String TEXT    = "#e8eaf2";
    private static final String TEXT3   = "#5e6482";
    private static final String ROSE    = "#f5697b";

    private Scene scene;

    public LoginPage(Stage stage) {

        //Title
        Label title = new Label("ClassHub");
        title.setStyle(
                "-fx-font-size:26px;" +
                        "-fx-font-weight:700;" +
                        "-fx-font-family:'Segoe UI';" +
                        "-fx-text-fill:" + TEXT + ";"
        );

        Label subtitle = new Label("Sign in to your account");
        subtitle.setStyle(
                "-fx-font-size:13px;" +
                        "-fx-font-family:'Segoe UI';" +
                        "-fx-text-fill:" + TEXT3 + ";"
        );

        //Fields
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.setMaxWidth(Double.MAX_VALUE);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setMaxWidth(Double.MAX_VALUE);

        //Buttons
        Button loginBtn = new Button("Login");
        loginBtn.setMaxWidth(Double.MAX_VALUE);

        Hyperlink createAccountLink = new Hyperlink("Create Account");

        // error label
        Label errorLabel = new Label("");
        errorLabel.setStyle("-fx-text-fill:" + ROSE + ";-fx-font-size:12px;-fx-font-family:'Segoe UI';");
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

        createAccountLink.setOnAction(e -> {
            stage.setScene(new RegisterPage(stage).getScene());
        });

        //Card
        VBox card = new VBox(14);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(40, 44, 40, 44));
        card.setMaxWidth(400);

        card.setStyle(
                "-fx-background-color:" + SURFACE + ";" +
                        "-fx-border-color:" + BORDER + ";" +
                        "-fx-border-width:1;" +
                        "-fx-border-radius:14;" +
                        "-fx-background-radius:14;"
        );

        card.getChildren().addAll(title, subtitle, emailField, passwordField, loginBtn, createAccountLink, errorLabel);

        //Root
        StackPane root = new StackPane(card);
        root.setStyle("-fx-background-color:" + BG + ";");

        scene = new Scene(root, 900, 600);
    }

    public Scene getScene() {
        return scene;
    }
}