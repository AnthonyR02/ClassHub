package org.example.classhub;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
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
                "-fx-font-size:36px;" +
                        "-fx-font-weight:700;" +
                        "-fx-font-family:'Segoe UI';" +
                        "-fx-text-fill:" + TEXT + ";"
        );

        Label subtitle = new Label("Sign in to your account");
        subtitle.setStyle(
                "-fx-font-size:16px;" +     // was 13
                        "-fx-font-family:'Segoe UI';" +
                        "-fx-text-fill:" + TEXT3 + ";"
        );

        //Fields
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.setMaxWidth(Double.MAX_VALUE);
        emailField.setStyle("-fx-font-size:14px;-fx-padding:10 14 10 14;");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setMaxWidth(Double.MAX_VALUE);
        passwordField.setStyle("-fx-font-size:14px;-fx-padding:10 14 10 14;");

        //Buttons
        Button loginBtn = new Button("Login");
        loginBtn.setMaxWidth(Double.MAX_VALUE);
        loginBtn.setStyle("-fx-font-size:14px;-fx-padding:10 0 10 0;");

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

            try {
                FirebaseAuthClient client = new FirebaseAuthClient();

                String idToken = client.signIn(email, pass);

                client.verifyWithSpring(idToken);

                // show success
                errorLabel.setVisible(false);
                errorLabel.setManaged(false);
                stage.setScene(ClassHubApplication.dashboardScene);

            } catch (Exception ex) {
                ex.printStackTrace();
                String message = ex.getMessage();
                if (message.contains("INVALID_EMAIL") || message.contains("INVALID_PASSWORD")) {
                    errorLabel.setText("Invalid email or password");
                } else if (message.contains("TOO_MANY_ATTEMPTS")) {
                    errorLabel.setText("Too many attempts, please try again later");
                } else {
                    errorLabel.setText("Could not connect to server.");
                }
                errorLabel.setVisible(true);
                errorLabel.setManaged(true);
            }

        });

        createAccountLink.setOnAction(e -> {
            stage.setScene(new RegisterPage(stage).getScene());
        });

        //Card
        VBox card = new VBox(14);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(60, 80, 60, 80));
        card.setMaxWidth(700);
        card.setMaxHeight(Region.USE_PREF_SIZE);

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
        root.setMinWidth(Double.MAX_VALUE);
        root.setMinHeight(Double.MAX_VALUE);
        StackPane.setAlignment(card, Pos.CENTER);

        scene = new Scene(root, 1280, 800);
    }

    public Scene getScene() {
        return scene;
    }
}