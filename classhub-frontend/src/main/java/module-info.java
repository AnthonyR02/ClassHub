module org.example.classhub {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;

    opens org.example.classhub to javafx.fxml;
    exports org.example.classhub;
}