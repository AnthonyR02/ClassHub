module com.classhub {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    opens com.classhub to javafx.fxml;
    opens com.classhub.models to javafx.fxml;
    opens com.classhub.views to javafx.fxml;

    exports com.classhub;
    exports com.classhub.models;
    exports com.classhub.utils;
    exports com.classhub.views;
}
