package loginscreen;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class DashboardController {

    // ===== UI ELEMENTS FROM FXML =====
    @FXML
    private Label sectionTitle;

    @FXML
    private StackPane contentArea;

    // ===== DARK MODE STATE =====
    private boolean darkMode = false;

    // ===== DARK MODE TOGGLE =====
    @FXML
    public void toggleDarkMode(ActionEvent event) {
        Scene scene = ((Node) event.getSource()).getScene();

        String css = getClass()
                .getResource("/org/example/loginscreen/dark-theme.css")
                .toExternalForm();

        if (!darkMode) {
            scene.getStylesheets().add(css);
        } else {
            scene.getStylesheets().remove(css);
        }

        darkMode = !darkMode;
    }

    // ===== SHOW ADD TASK VIEW =====
    @FXML
    public void showAddTask() {
        sectionTitle.setText("Add Task");

        VBox view = new VBox(10);
        view.getChildren().addAll(
                new Label("Task Name"),
                new TextField(),
                new Button("Submit")
        );

        contentArea.getChildren().setAll(view);
    }

    // ===== SHOW ADD COURSE VIEW =====
    @FXML
    public void showAddCourse() {
        sectionTitle.setText("Add Course");

        VBox view = new VBox(10);
        view.getChildren().addAll(
                new Label("Course Name"),
                new TextField(),
                new Button("Add")
        );

        contentArea.getChildren().setAll(view);
    }

    // ===== OPTIONAL: DEFAULT VIEW WHEN LOADED =====
    @FXML
    public void initialize() {
        sectionTitle.setText("Dashboard");

        VBox defaultView = new VBox(10);
        defaultView.getChildren().add(
                new Label("Select an option from the sidebar")
        );

        contentArea.getChildren().setAll(defaultView);
    }
}