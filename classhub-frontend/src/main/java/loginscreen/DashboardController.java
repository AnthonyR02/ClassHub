package loginscreen;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class DashboardController {

    @FXML private Label sectionTitle;
    @FXML private StackPane contentArea;
    @FXML private Label userNameLabel;

    @FXML
    public void initialize() {
        // Default view — content area is hidden when dashboard cards are visible
        if (contentArea != null) {
            contentArea.setVisible(false);
            contentArea.setManaged(false);
        }
    }

    @FXML
    public void showDashboard() {
        sectionTitle.setText("Dashboard");
        if (contentArea != null) {
            contentArea.setVisible(false);
            contentArea.setManaged(false);
        }
    }

    @FXML
    public void showCalendar() {
        sectionTitle.setText("Smart Calendar");
        showPlaceholder("Calendar view coming soon.");
    }

    @FXML
    public void showAssignments() {
        sectionTitle.setText("Assignments");
        showPlaceholder("Assignments view coming soon.");
    }

    @FXML
    public void showGrades() {
        sectionTitle.setText("Grades");
        showPlaceholder("Grades view coming soon.");
    }

    @FXML
    public void showNotes() {
        sectionTitle.setText("Notes");
        showPlaceholder("Notes view coming soon.");
    }

    @FXML
    public void showAddTask() {
        sectionTitle.setText("Add Task");
        VBox view = new VBox(10);
        view.getChildren().addAll(new Label("Task Name"), new TextField(), new Button("Submit"));
        if (contentArea != null) {
            contentArea.getChildren().setAll(view);
            contentArea.setVisible(true);
            contentArea.setManaged(true);
        }
    }

    @FXML
    public void showAddCourse() {
        sectionTitle.setText("Add Course");
        VBox view = new VBox(10);
        view.getChildren().addAll(new Label("Course Name"), new TextField(), new Button("Add"));
        if (contentArea != null) {
            contentArea.getChildren().setAll(view);
            contentArea.setVisible(true);
            contentArea.setManaged(true);
        }
    }

    @FXML
    public void handleLogout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login-view.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 900, 600);
            scene.getStylesheets().add(getClass().getResource("/dark-theme.css").toExternalForm());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Kept for backwards compat with old FXML
    @FXML
    public void toggleDarkMode(ActionEvent event) { /* already dark by default */ }

    private void showPlaceholder(String message) {
        if (contentArea == null) return;
        VBox view = new VBox(10);
        Label lbl = new Label(message);
        lbl.setStyle("-fx-text-fill: #8b949e; -fx-font-size: 14px;");
        view.getChildren().add(lbl);
        contentArea.getChildren().setAll(view);
        contentArea.setVisible(true);
        contentArea.setManaged(true);
    }
}
