module org.example.loginscreen {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;

    opens org.example.loginscreen to javafx.fxml;
    exports org.example.loginscreen;
}