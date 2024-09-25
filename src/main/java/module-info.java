module org.example.quanlithuvien {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;
    //requires mysql.connection.java;


    opens org.example.quanlithuvien to javafx.fxml;
    exports org.example.quanlithuvien;

    opens login to javafx.fxml;
    exports login;



}