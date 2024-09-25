module org.example.quanlithuvien {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.quanlithuvien to javafx.fxml;
    exports org.example.quanlithuvien;

    opens login to javafx.fxml;
    exports login;

}