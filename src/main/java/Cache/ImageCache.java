package Cache;

import javafx.scene.image.Image;
import java.io.ByteArrayInputStream;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static Database.DatabaseConnection.getConnection;  // Kết nối cơ sở dữ liệu

public class ImageCache {
    private static final Map<String, Image> cache = new HashMap<>();
    private static ImageCache instance;
    private static final ExecutorService executorService = Executors.newFixedThreadPool(12);  // Giới hạn 10 luồng tải

    private ImageCache() {
        // Constructor riêng tư
    }

    public static ImageCache getInstance() {
        if (instance == null) {
            instance = new ImageCache();
        }
        return instance;
    }

    public static Image getImage(String path) {
        return cache.computeIfAbsent(path, Image::new);
    }

    // Phương thức tải hình ảnh từ cơ sở dữ liệu song song
    public void loadImagesFromDatabase() {
        System.out.println("Bat dau load anh");
        String sql = "SELECT imagePath, id FROM Books";  // Câu truy vấn lấy thông tin hình ảnh

        executorService.submit(() -> {
            try (Connection connection = getConnection();
                 Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(sql)) {

                while (resultSet.next()) {
                    String imagePath = resultSet.getString("imagePath");
                    int bookId = resultSet.getInt("id");

                    if (imagePath != null && !imagePath.isEmpty()) {
                        // Tải hình ảnh trong nền
                        executorService.submit(() -> loadImageToCache(imagePath, bookId));
                    }
                }
                System.out.println("Load anh thanh cong");  // In ra thông báo khi hoàn thành

            } catch (SQLException e) {
                e.printStackTrace();  // Log lỗi nếu có
            }
        });
    }

    // Phương thức tải từng hình ảnh vào cache
    private void loadImageToCache(String imagePath, int bookId) {
        Image image = new Image(imagePath);
        cache.put("image_" + bookId, image);  // Lưu hình ảnh vào cache
    }
}
