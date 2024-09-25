package org.example.quanlithuvien;//package org.example.quanlithuvien;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class haubeo extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(haubeo.class.getResource("login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 560, 402);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}