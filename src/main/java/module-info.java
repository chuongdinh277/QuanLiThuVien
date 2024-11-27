module org.example.src {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires com.google.gson;
    requires java.sql;
    requires java.net.http;

    opens Controllers to javafx.fxml;
    exports Controllers;

    opens APIGoogle;
    exports APIGoogle;

    opens Document;
    exports Document;
    exports Cache;
    opens Cache;

}