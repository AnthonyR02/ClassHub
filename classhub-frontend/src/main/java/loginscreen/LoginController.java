package loginscreen;

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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../resources/hello-view.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //submit button to dashboard
    public void goToDashboard(ActionEvent event) {
        String email = usernameField.getText();
        String password = passwordField.getText();


        try {
            HttpClient client = HttpClient.newHttpClient();

            String body = "{\"email\":\"" + email + "\",\"password\":\"" + password + "\"}";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:8080/api/auth/login"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());


        if(response.statusCode() == 200) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("../resources/dashboard-view.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
        } else{
            errorLabel.setText("Incorrect email or password.");
        }

        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText("Could not connect to server");
        }
    }
}