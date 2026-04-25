package org.example.loginscreen;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Scene;

public class DashboardController {

    private boolean darkMode = false;

    public void toggleDarkMode(ActionEvent event) {
        Scene scene = ((Node) event.getSource()).getScene();

        if (!darkMode) {
            scene.getStylesheets().add(
                    getClass().getResource("dark-theme.css").toExternalForm()
            );
        } else {
            scene.getStylesheets().clear();
        }

        darkMode = !darkMode;
    }
}