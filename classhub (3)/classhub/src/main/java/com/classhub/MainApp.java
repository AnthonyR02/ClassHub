package com.classhub;

import com.classhub.views.MainWindow;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) {
        MainWindow window = new MainWindow();
        Scene scene = new Scene(window, 1200, 780);
        stage.setTitle("ClassHub");
        stage.setScene(scene);
        stage.setMinWidth(900);
        stage.setMinHeight(600);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
