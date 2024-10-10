module org.example.src {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires com.google.gson;
    requires java.sql;
    requires java.net.http;

    opens controllers to javafx.fxml;
    exports controllers;

    opens APIGoogle;
    exports APIGoogle;

    opens Document;
    exports Document;


}