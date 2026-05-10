package loginscreen;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader =
                new FXMLLoader(HelloApplication.class.getResource("/login-view.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 900, 600);

        // Apply dark theme stylesheet
        String css = HelloApplication.class.getResource("/dark-theme.css").toExternalForm();
        scene.getStylesheets().add(css);

        stage.setTitle("ClassHub");
        stage.setScene(scene);
        stage.show();
    }
}
