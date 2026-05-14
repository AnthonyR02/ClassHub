package org.example.classhub;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
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
    private static final String GREEN   = "#3ecfb0";

    private Scene scene;

    // Default constructor — no banner message
    public LoginPage(Stage stage) {
        this(stage, null);
    }

    // Constructor with optional success banner (e.g. after registration)
    public LoginPage(Stage stage, String successMessage) {

        // Title
        Label title = new Label("ClassHub");
        title.setStyle(
            "-fx-font-size:36px;" +
            "-fx-font-weight:700;" +
            "-fx-font-family:'Segoe UI';" +
            "-fx-text-fill:" + TEXT + ";"
        );

        Label subtitle = new Label("Sign in to your account");
        subtitle.setStyle(
            "-fx-font-size:16px;" +
            "-fx-font-family:'Segoe UI';" +
            "-fx-text-fill:" + TEXT3 + ";"
        );

        // Success banner (shown after registration)
        Label successLabel = new Label(successMessage != null ? "✔  " + successMessage : "");
        successLabel.setStyle(
            "-fx-text-fill:" + GREEN + ";" +
            "-fx-font-size:12px;" +
            "-fx-font-family:'Segoe UI';" +
            "-fx-background-color:rgba(62,207,176,0.1);" +
            "-fx-background-radius:8;" +
            "-fx-padding:8 12 8 12;"
        );
        successLabel.setMaxWidth(Double.MAX_VALUE);
        successLabel.setVisible(successMessage != null);
        successLabel.setManaged(successMessage != null);

        // Fields
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.setMaxWidth(Double.MAX_VALUE);
        emailField.setStyle("-fx-font-size:14px;-fx-padding:10 14 10 14;");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setMaxWidth(Double.MAX_VALUE);
        passwordField.setStyle("-fx-font-size:14px;-fx-padding:10 14 10 14;");

        // Login button
        Button loginBtn = new Button("Login");
        loginBtn.setMaxWidth(Double.MAX_VALUE);
        loginBtn.setStyle("-fx-font-size:14px;-fx-padding:10 0 10 0;");

        Hyperlink createAccountLink = new Hyperlink("Create Account");

        // Error label
        Label errorLabel = new Label("");
        errorLabel.setStyle("-fx-text-fill:" + ROSE + ";-fx-font-size:12px;-fx-font-family:'Segoe UI';");
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);

        // Login logic — runs on a background thread so the UI doesn't freeze
        loginBtn.setOnAction(e -> {
            String email = emailField.getText().trim();
            String pass  = passwordField.getText();

            if (email.isEmpty() || pass.isEmpty()) {
                errorLabel.setText("Please fill in all fields.");
                errorLabel.setVisible(true);
                errorLabel.setManaged(true);
                return;
            }

            // Hide any previous messages and show loading state
            errorLabel.setVisible(false);
            errorLabel.setManaged(false);
            successLabel.setVisible(false);
            successLabel.setManaged(false);
            loginBtn.setDisable(true);
            loginBtn.setText("Signing in…");

            Thread thread = new Thread(() -> {
                try {
                    FirebaseAuthClient client = new FirebaseAuthClient();
                    client.login(email, pass); // JWT stored in SessionManager inside login()

                    javafx.application.Platform.runLater(() -> {
                        ClassHubApplication.buildAppScenes(stage);
                        stage.setScene(ClassHubApplication.dashboardScene);
                    });

                } catch (Exception ex) {
                    ex.printStackTrace();
                    javafx.application.Platform.runLater(() -> {
                        loginBtn.setDisable(false);
                        loginBtn.setText("Login");
                        String msg = ex.getMessage() != null ? ex.getMessage() : "";
                        if (msg.contains("Invalid email or password") || msg.contains("INVALID_LOGIN_CREDENTIALS")
                                || msg.contains("Unauthorized") || msg.contains("401")) {
                            errorLabel.setText("Invalid email or password.");
                        } else if (msg.contains("Connection refused") || msg.contains("reach the server")) {
                            errorLabel.setText("Could not reach the server. Is the backend running?");
                        } else {
                            errorLabel.setText("Sign-in failed. Please check your credentials.");
                        }
                        errorLabel.setVisible(true);
                        errorLabel.setManaged(true);
                    });
                }
            });
            thread.setDaemon(true);
            thread.start();
        });

        // Allow Enter key on password field to submit
        passwordField.setOnAction(e -> loginBtn.fire());

        createAccountLink.setOnAction(e ->
            stage.setScene(new RegisterPage(stage).getScene())
        );

        // DEV BYPASS — skip Firebase entirely, go straight to dashboard
        // Remove this button before final submission
        Button devBtn = new Button("[ Dev: Skip Login ]");
        devBtn.setStyle(
            "-fx-background-color:transparent;" +
            "-fx-text-fill:#5e6482;" +
            "-fx-font-size:11px;" +
            "-fx-font-family:'Segoe UI';" +
            "-fx-border-color:#ffffff12;" +
            "-fx-border-width:1;" +
            "-fx-border-radius:6;" +
            "-fx-padding:4 10 4 10;" +
            "-fx-cursor:hand;"
        );
        devBtn.setOnAction(e -> {
            // Inject a fake session so all pages have something to work with
            SessionManager.login("dev-uid-001", "Dev User", "STUDENT", "dev@classhub.local");
            SessionManager.setIdToken("dev-token-bypass");
            ClassHubApplication.buildAppScenes(stage);
            stage.setScene(ClassHubApplication.dashboardScene);
        });

        // Card
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

        card.getChildren().addAll(
            title, subtitle,
            successLabel,
            emailField, passwordField,
            loginBtn, createAccountLink,
            errorLabel,
            devBtn
        );

        // Root
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
