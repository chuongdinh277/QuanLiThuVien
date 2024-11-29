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
        // Tải cache từ tệp trước khi load từ cơ sở dữ liệu
        BookCache.getInstance().loadCacheFromFile();
        // Nếu cache trống, tải từ cơ sở dữ liệu
        if (BookCache.getInstance().isCached("someISBN")) { // Kiểm tra ví dụ (có thể thay đổi)
            BookCache.getInstance().loadBooksFromDatabase();
        }

        ImageCache.getInstance().loadImagesFromDatabase();

        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/views/login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 600);
        stage.setTitle("VERSION 1.0");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() {
        BookCache.getInstance().saveCacheToFile(); // Lưu cache vào tệp khi thoát
    }

    public static void main(String[] args) {
        launch();
    }
}