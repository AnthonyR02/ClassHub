module org.example.loginscreen {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.loginscreen to javafx.fxml;
    exports org.example.loginscreen;
}