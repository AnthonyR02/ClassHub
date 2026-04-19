package org.example.loginscreen;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));

        //window size and title
        Scene scene = new Scene(fxmlLoader.load(), 640, 480);
        stage.setTitle("ClassHub");
        stage.setScene(scene);
        stage.show();
    }
}
