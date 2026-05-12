package org.example.loginscreen;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class LoginController {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label errorLabel; // shows error if login fails

    public void goBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void goToDashboard(ActionEvent event) {
        String email = usernameField.getText();
        String password = passwordField.getText();

        if(usernameField.getText().isEmpty() || passwordField.getText().isEmpty()) {
            errorLabel.setText("Please enter your email and password");
            return;
        }

        try {
            FirebaseAuthClient client = new FirebaseAuthClient();

            String idToken = client.signIn(email, password);

            client.verifyWithSpring(idToken);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("dashboard-view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));

        } catch (Exception e) {
            e.printStackTrace();
            String message = e.getMessage();
            if (message.contains("INVALID_LOGIN_CREDENTIALS")) {
                errorLabel.setText("Invalid email or password");
            } else if (message.contains("TOO_MANY_ATTEMPTS")) {
                errorLabel.setText("Too many attempts, please try again later");
            } else {
                errorLabel.setText("Could not connect to server.");
            }
        }
    }
}