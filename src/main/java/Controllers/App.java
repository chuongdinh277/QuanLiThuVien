package Controllers;

import Cache.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class App extends Application {
    /**
     * Initializes the application, loads cache data, and sets up the main scene.
     *
     * @param stage The primary stage for the application.
     * @throws IOException If an error occurs while loading the FXML file.
     */
    @Override
    public void start(Stage stage) throws IOException {
        BookCache.getInstance().loadCacheFromFile();

        if (BookCache.getInstance().isCached("someISBN")) { // Check for example ISBN
            BookCache.getInstance().loadBooksFromDatabase();
        }

        ImageCache.getInstance().loadImagesFromDatabase();

        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/views/login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 600);
        stage.setTitle("VERSION 1.0");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Stops the application and saves the cache to file.
     */
    @Override
    public void stop() {
        BookCache.getInstance().saveCacheToFile();
    }

    /**
     * Main entry point to launch the application.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        launch();
    }
}
