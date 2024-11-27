package Controllers;

import Cache.ImageCache;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import Cache.BookCache;

public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        BookCache.getInstance().loadBooksFromDatabase();
        ImageCache.getInstance().loadImagesFromDatabase();

        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/views/login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 600);
        stage.setTitle("VERSION 1.0");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}