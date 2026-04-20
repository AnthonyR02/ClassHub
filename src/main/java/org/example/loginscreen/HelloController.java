package org.example.loginscreen;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class HelloController {

    @FXML

    //when clicking on the login button, is supposed to take the user to the login screen (user and pass)
    // as for now, it does not work. I still have to find the issue and make a loginview fxml file
    public void goToLogin(ActionEvent event) {
        //this gives the button functionality to take the user to the login screen
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login-view.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
