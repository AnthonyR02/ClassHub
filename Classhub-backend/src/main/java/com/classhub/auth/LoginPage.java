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

        // Title
        Label title = new Label("ClassHub Login");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // Username field
        TextField username = new TextField();
        username.setPromptText("Username");
        username.setMaxWidth(250);

        // Password field
        PasswordField password = new PasswordField();
        password.setPromptText("Password");
        password.setMaxWidth(250);

        // Login button
        Button loginBtn = new Button("Login");
        loginBtn.setMaxWidth(250);

        // Register button
        Button registerBtn = new Button("Go to Register");
        registerBtn.setMaxWidth(250);

        //LOGIN LOGIC (for now hardcoded)
        loginBtn.setOnAction(e -> {
            String user = username.getText();
            String pass = password.getText();

            if (user.equals("admin") && pass.equals("1234")) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Success");
                alert.setHeaderText(null);
                alert.setContentText("Login successful!");
                alert.show();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Invalid username or password");
                alert.show();
            }
        });

        // GO TO REGISTER PAGE
        registerBtn.setOnAction(e -> {
            stage.setScene(new RegisterPage(stage).getScene());
        });

        // Layout
        VBox root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));

        root.getChildren().addAll(title, username, password, loginBtn, registerBtn);

        // Scene
        scene = new Scene(root, 400, 300);
    }

    public Scene getScene() {
        return scene;
    }
}