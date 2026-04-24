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

        /* Made the starting scene match the other 2 scenes in size from H:680->600, W:480->400*/
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setTitle("ClassHub");
        stage.setScene(scene);
        stage.show();
    }
}
