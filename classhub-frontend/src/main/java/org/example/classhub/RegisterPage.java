package org.example.classhub;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class RegisterPage {

    private static final String BG      = "#0f1117";
    private static final String SURFACE = "#181c27";
    private static final String BORDER  = "#ffffff12";
    private static final String TEXT    = "#e8eaf2";
    private static final String TEXT3   = "#5e6482";
    private static final String ACCENT  = "#6c8ef5";
    private static final String ROSE    = "#f5697b";

    private Scene scene;

    public RegisterPage(Stage stage) {

        Label title = new Label("ClassHub");
        title.setStyle(
                "-fx-font-size:26px;" +
                        "-fx-font-weight:700;" +
                        "-fx-font-family:'Segoe UI';" +
                        "-fx-text-fill:" + TEXT + ";"
        );

        Label subtitle = new Label("Create your account");
        subtitle.setStyle(
                "-fx-font-size:13px;" +
                        "-fx-font-family:'Segoe UI';" +
                        "-fx-text-fill:" + TEXT3 + ";"
        );

        // Fields
        TextField nameField = new TextField();
        nameField.setPromptText("Full Name");
        nameField.setMaxWidth(Double.MAX_VALUE);
        styleField(nameField);

        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.setMaxWidth(Double.MAX_VALUE);
        styleField(emailField);

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setMaxWidth(Double.MAX_VALUE);
        styleField(passwordField);

        PasswordField confirmField = new PasswordField();
        confirmField.setPromptText("Confirm Password");
        confirmField.setMaxWidth(Double.MAX_VALUE);
        styleField(confirmField);

        // Error label
        Label errorLabel = new Label("");
        errorLabel.setStyle("-fx-text-fill:" + ROSE + ";-fx-font-size:12px;-fx-font-family:'Segoe UI';");
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);

        // Register button
        Button registerBtn = new Button("Create Account");
        registerBtn.setMaxWidth(Double.MAX_VALUE);
        registerBtn.setStyle(
                "-fx-background-color:" + ACCENT + ";" +
                        "-fx-text-fill:white;" +
                        "-fx-font-size:13px;" +
                        "-fx-font-weight:600;" +
                        "-fx-font-family:'Segoe UI';" +
                        "-fx-background-radius:8;" +
                        "-fx-padding:9 0 9 0;" +
                        "-fx-cursor:hand;"
        );

        registerBtn.setOnAction(e -> {
            String name    = nameField.getText().trim();
            String email   = emailField.getText().trim();
            String pass    = passwordField.getText();
            String confirm = confirmField.getText();

            if (name.isEmpty() || email.isEmpty() || pass.isEmpty() || confirm.isEmpty()) {
                errorLabel.setText("Please fill in all fields.");
                errorLabel.setVisible(true);
                errorLabel.setManaged(true);
                return;
            }
            if (!pass.equals(confirm)) {
                errorLabel.setText("Passwords do not match.");
                errorLabel.setVisible(true);
                errorLabel.setManaged(true);
                return;
            }
            // FIX: On success, navigate to dashboard (was doing nothing before)
            errorLabel.setVisible(false);
            errorLabel.setManaged(false);
            stage.setScene(new Dashboard(stage).getScene());
        });

        // Allow Enter key on confirm field to submit
        confirmField.setOnAction(e -> registerBtn.fire());

        Hyperlink backLink = new Hyperlink("Back to Login");
        backLink.setStyle(
                "-fx-text-fill:" + TEXT3 + ";" +
                        "-fx-font-size:12px;" +
                        "-fx-font-family:'Segoe UI';" +
                        "-fx-border-color:transparent;"
        );
        backLink.setOnAction(e -> stage.setScene(new LoginPage(stage).getScene()));

        // Card
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
        card.getChildren().addAll(title, subtitle, nameField, emailField, passwordField, confirmField, errorLabel, registerBtn, backLink);

        // Root
        StackPane root = new StackPane(card);
        root.setStyle("-fx-background-color:" + BG + ";");

        scene = new Scene(root, 900, 600);
    }

    private void styleField(Control field) {
        field.setStyle(
                "-fx-background-color:#1f2436;" +
                        "-fx-text-fill:#e8eaf2;" +
                        "-fx-prompt-text-fill:#5e6482;" +
                        "-fx-border-color:#ffffff20;" +
                        "-fx-border-width:1;" +
                        "-fx-border-radius:8;" +
                        "-fx-background-radius:8;" +
                        "-fx-padding:8 12 8 12;" +
                        "-fx-font-size:13px;" +
                        "-fx-font-family:'Segoe UI';"
        );
    }

    public Scene getScene() {
        return scene;
    }
}