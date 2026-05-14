package org.example.classhub;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class RegisterPage {

    private static final String BG      = "#0f1117";
    private static final String SURFACE = "#181c27";
    private static final String BORDER  = "#ffffff12";
    private static final String TEXT    = "#e8eaf2";
    private static final String TEXT3   = "#5e6482";
    private static final String ACCENT  = "#6c8ef5";
    private static final String ROSE    = "#f5697b";
    private static final String GREEN   = "#3ecfb0";
    private static final String FIELD_BG = "#1f2436";

    private Scene scene;

    // Password requirement flags
    private boolean hasLength   = false;
    private boolean hasUpper    = false;
    private boolean hasSpecial  = false;

    public RegisterPage(Stage stage) {

        Label title = new Label("ClassHub");
        title.setStyle("-fx-font-size:26px;-fx-font-weight:700;-fx-font-family:'Segoe UI';-fx-text-fill:" + TEXT + ";");

        Label subtitle = new Label("Create your account");
        subtitle.setStyle("-fx-font-size:13px;-fx-font-family:'Segoe UI';-fx-text-fill:" + TEXT3 + ";");

        // ── Fields ──────────────────────────────────────────────────────────
        TextField nameField = new TextField();
        nameField.setPromptText("Full Name");
        nameField.setMaxWidth(Double.MAX_VALUE);
        styleField(nameField);

        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.setMaxWidth(Double.MAX_VALUE);
        styleField(emailField);

        // Password row: PasswordField + visible TextField stacked, plus eye button
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        styleField(passwordField);

        TextField passwordVisible = new TextField();
        passwordVisible.setPromptText("Password");
        styleField(passwordVisible);
        passwordVisible.setVisible(false);
        passwordVisible.setManaged(false);

        // Keep the two fields in sync
        passwordField.textProperty().addListener((obs, o, n) -> {
            if (!passwordVisible.isFocused()) passwordVisible.setText(n);
            checkRequirements(n);
        });
        passwordVisible.textProperty().addListener((obs, o, n) -> {
            if (!passwordField.isFocused()) passwordField.setText(n);
            checkRequirements(n);
        });

        Button eyeBtn = new Button("👁");
        eyeBtn.setStyle(
            "-fx-background-color:transparent;" +
            "-fx-text-fill:" + TEXT3 + ";" +
            "-fx-font-size:14px;" +
            "-fx-cursor:hand;" +
            "-fx-padding:0 6 0 6;"
        );
        eyeBtn.setFocusTraversable(false);

        // Toggle visibility
        final boolean[] showing = {false};
        eyeBtn.setOnAction(e -> {
            showing[0] = !showing[0];
            if (showing[0]) {
                passwordVisible.setText(passwordField.getText());
                passwordField.setVisible(false);   passwordField.setManaged(false);
                passwordVisible.setVisible(true);  passwordVisible.setManaged(true);
                eyeBtn.setStyle(eyeBtn.getStyle().replace(TEXT3, ACCENT));
            } else {
                passwordField.setText(passwordVisible.getText());
                passwordVisible.setVisible(false); passwordVisible.setManaged(false);
                passwordField.setVisible(true);    passwordField.setManaged(true);
                eyeBtn.setStyle(eyeBtn.getStyle().replace(ACCENT, TEXT3));
            }
        });

        StackPane passStack = new StackPane();
        HBox.setHgrow(passStack, Priority.ALWAYS);
        StackPane.setAlignment(passwordField,   Pos.CENTER_LEFT);
        StackPane.setAlignment(passwordVisible, Pos.CENTER_LEFT);
        passStack.getChildren().addAll(passwordField, passwordVisible);

        HBox passRow = new HBox(0, passStack, eyeBtn);
        passRow.setAlignment(Pos.CENTER);
        passRow.setMaxWidth(Double.MAX_VALUE);
        passRow.setStyle(
            "-fx-background-color:" + FIELD_BG + ";" +
            "-fx-border-color:#ffffff20;" +
            "-fx-border-width:1;" +
            "-fx-border-radius:8;" +
            "-fx-background-radius:8;"
        );
        HBox.setHgrow(passRow, Priority.ALWAYS);

        // ── Password requirements ────────────────────────────────────────────
        Label reqLength  = reqLabel("At least 8 characters");
        Label reqUpper   = reqLabel("One uppercase letter");
        Label reqSpecial = reqLabel("One special character (!@#$%^&* etc.)");

        VBox requirements = new VBox(4, reqLength, reqUpper, reqSpecial);
        requirements.setPadding(new Insets(8, 0, 4, 0));

        // ── Confirm password row ─────────────────────────────────────────────
        PasswordField confirmField = new PasswordField();
        confirmField.setPromptText("Confirm Password");
        confirmField.setMaxWidth(Double.MAX_VALUE);
        styleField(confirmField);

        TextField confirmVisible = new TextField();
        confirmVisible.setPromptText("Confirm Password");
        styleField(confirmVisible);
        confirmVisible.setVisible(false);
        confirmVisible.setManaged(false);

        confirmField.textProperty().addListener((obs, o, n) -> {
            if (!confirmVisible.isFocused()) confirmVisible.setText(n);
        });
        confirmVisible.textProperty().addListener((obs, o, n) -> {
            if (!confirmField.isFocused()) confirmField.setText(n);
        });

        Button eyeBtn2 = new Button("👁");
        eyeBtn2.setStyle(
            "-fx-background-color:transparent;" +
            "-fx-text-fill:" + TEXT3 + ";" +
            "-fx-font-size:14px;" +
            "-fx-cursor:hand;" +
            "-fx-padding:0 6 0 6;"
        );
        eyeBtn2.setFocusTraversable(false);

        final boolean[] showing2 = {false};
        eyeBtn2.setOnAction(e -> {
            showing2[0] = !showing2[0];
            if (showing2[0]) {
                confirmVisible.setText(confirmField.getText());
                confirmField.setVisible(false);   confirmField.setManaged(false);
                confirmVisible.setVisible(true);  confirmVisible.setManaged(true);
                eyeBtn2.setStyle(eyeBtn2.getStyle().replace(TEXT3, ACCENT));
            } else {
                confirmField.setText(confirmVisible.getText());
                confirmVisible.setVisible(false); confirmVisible.setManaged(false);
                confirmField.setVisible(true);    confirmField.setManaged(true);
                eyeBtn2.setStyle(eyeBtn2.getStyle().replace(ACCENT, TEXT3));
            }
        });

        StackPane confStack = new StackPane();
        HBox.setHgrow(confStack, Priority.ALWAYS);
        StackPane.setAlignment(confirmField,   Pos.CENTER_LEFT);
        StackPane.setAlignment(confirmVisible, Pos.CENTER_LEFT);
        confStack.getChildren().addAll(confirmField, confirmVisible);

        HBox confRow = new HBox(0, confStack, eyeBtn2);
        confRow.setAlignment(Pos.CENTER);
        confRow.setMaxWidth(Double.MAX_VALUE);
        confRow.setStyle(
            "-fx-background-color:" + FIELD_BG + ";" +
            "-fx-border-color:#ffffff20;" +
            "-fx-border-width:1;" +
            "-fx-border-radius:8;" +
            "-fx-background-radius:8;"
        );

        // ── Error label ──────────────────────────────────────────────────────
        Label errorLabel = new Label("");
        errorLabel.setStyle("-fx-text-fill:" + ROSE + ";-fx-font-size:12px;-fx-font-family:'Segoe UI';");
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
        errorLabel.setWrapText(true);

        // ── Register button ──────────────────────────────────────────────────
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

        // Helper to get current password regardless of which field is active
        registerBtn.setOnAction(e -> {
            String name    = nameField.getText().trim();
            String email   = emailField.getText().trim();
            String pass    = showing[0] ? passwordVisible.getText() : passwordField.getText();
            String confirm = showing2[0] ? confirmVisible.getText() : confirmField.getText();

            // Basic blank check
            if (name.isEmpty() || email.isEmpty() || pass.isEmpty() || confirm.isEmpty()) {
                showError(errorLabel, "Please fill in all fields.");
                return;
            }

            // Password requirements
            if (!hasLength) {
                showError(errorLabel, "Password must be at least 8 characters.");
                return;
            }
            if (!hasUpper) {
                showError(errorLabel, "Password must contain at least one uppercase letter.");
                return;
            }
            if (!hasSpecial) {
                showError(errorLabel, "Password must contain at least one special character.");
                return;
            }

            // Confirm match
            if (!pass.equals(confirm)) {
                showError(errorLabel, "Passwords do not match.");
                return;
            }

            errorLabel.setVisible(false);
            errorLabel.setManaged(false);
            registerBtn.setDisable(true);
            registerBtn.setText("Creating account…");

            final String finalPass = pass;
            Thread thread = new Thread(() -> {
                try {
                    FirebaseAuthClient client = new FirebaseAuthClient();
                    // register() now handles everything — creates account, stores JWT, sets session
                    client.register(name, email, finalPass, "STUDENT");

                    javafx.application.Platform.runLater(() -> {
                        ClassHubApplication.buildAppScenes(stage);
                        stage.setScene(ClassHubApplication.dashboardScene);
                    });

                } catch (Exception ex) {
                    ex.printStackTrace();
                    javafx.application.Platform.runLater(() -> {
                        registerBtn.setDisable(false);
                        registerBtn.setText("Create Account");
                        String msg = ex.getMessage() != null ? ex.getMessage() : "";
                        if (msg.contains("EMAIL_EXISTS") || msg.contains("email-already-in-use")) {
                            showError(errorLabel, "An account with this email already exists.");
                        } else if (msg.contains("WEAK_PASSWORD") || msg.contains("weak-password")) {
                            showError(errorLabel, "Password is too weak. Please follow the requirements.");
                        } else if (msg.contains("INVALID_EMAIL") || msg.contains("invalid-email")) {
                            showError(errorLabel, "Please enter a valid email address.");
                        } else if (msg.contains("Backend verification failed")) {
                            showError(errorLabel, "Could not reach the server. Is the backend running?");
                        } else {
                            showError(errorLabel, "Registration failed: " + (msg.isEmpty() ? "Please try again." : msg));
                        }
                    });
                }
            });
            thread.setDaemon(true);
            thread.start();
        });

        // Enter on confirm submits
        confirmField.setOnAction(ev -> registerBtn.fire());
        confirmVisible.setOnAction(ev -> registerBtn.fire());

        // ── Back link ────────────────────────────────────────────────────────
        Hyperlink backLink = new Hyperlink("Back to Login");
        backLink.setStyle(
            "-fx-text-fill:" + TEXT3 + ";" +
            "-fx-font-size:12px;" +
            "-fx-font-family:'Segoe UI';" +
            "-fx-border-color:transparent;"
        );
        backLink.setOnAction(e -> stage.setScene(new LoginPage(stage).getScene()));

        // Wire up live requirement labels now that they're in scope
        passwordField.textProperty().addListener((obs, o, n) ->
            updateReqLabels(reqLength, reqUpper, reqSpecial));
        passwordVisible.textProperty().addListener((obs, o, n) ->
            updateReqLabels(reqLength, reqUpper, reqSpecial));

        // ── Card ─────────────────────────────────────────────────────────────
        VBox card = new VBox(12);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(36, 44, 36, 44));
        card.setMaxWidth(420);
        card.setStyle(
            "-fx-background-color:" + SURFACE + ";" +
            "-fx-border-color:" + BORDER + ";" +
            "-fx-border-width:1;" +
            "-fx-border-radius:14;" +
            "-fx-background-radius:14;"
        );
        card.getChildren().addAll(
            title, subtitle,
            nameField, emailField,
            passRow, requirements,
            confRow,
            errorLabel, registerBtn, backLink
        );

        StackPane root = new StackPane(card);
        root.setStyle("-fx-background-color:" + BG + ";");

        scene = new Scene(root, 900, 700);
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private void checkRequirements(String pass) {
        hasLength  = pass.length() >= 8;
        hasUpper   = pass.chars().anyMatch(Character::isUpperCase);
        hasSpecial = pass.chars().anyMatch(c -> "!@#$%^&*()_+-=[]{}|;':\",./<>?`~\\".indexOf(c) >= 0);
    }

    private void updateReqLabels(Label len, Label upper, Label special) {
        applyReq(len,     hasLength,  "At least 8 characters");
        applyReq(upper,   hasUpper,   "One uppercase letter");
        applyReq(special, hasSpecial, "One special character (!@#$%^&* etc.)");
    }

    private void applyReq(Label lbl, boolean met, String text) {
        if (met) {
            lbl.setText("✔  " + text);
            lbl.setStyle("-fx-font-size:11px;-fx-font-family:'Segoe UI';-fx-text-fill:#3ecfb0;");
        } else {
            lbl.setText("✗  " + text);
            lbl.setStyle("-fx-font-size:11px;-fx-font-family:'Segoe UI';-fx-text-fill:#5e6482;");
        }
    }

    private Label reqLabel(String text) {
        Label l = new Label("✗  " + text);
        l.setStyle("-fx-font-size:11px;-fx-font-family:'Segoe UI';-fx-text-fill:#5e6482;");
        return l;
    }

    private void showError(Label lbl, String msg) {
        lbl.setText(msg);
        lbl.setVisible(true);
        lbl.setManaged(true);
    }

    private void styleField(Control field) {
        field.setStyle(
            "-fx-background-color:transparent;" +
            "-fx-text-fill:#e8eaf2;" +
            "-fx-prompt-text-fill:#5e6482;" +
            "-fx-border-color:transparent;" +
            "-fx-border-width:0;" +
            "-fx-padding:8 12 8 12;" +
            "-fx-font-size:13px;" +
            "-fx-font-family:'Segoe UI';"
        );
    }

    public Scene getScene() {
        return scene;
    }
}
