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
    private static final Map<String, Image> cache = new HashMap<>();  // Lưu trữ hình ảnh vào cache
    private static final ExecutorService executorService = Executors.newFixedThreadPool(4);  // Giới hạn 4 luồng tải
    private static ImageCache instance;

    private ImageCache() {
        // Constructor riêng tư để áp dụng Singleton
    }

    public static ImageCache getInstance() {
        if (instance == null) {
            instance = new ImageCache();
        }
        return instance;
    }

    /**
     * Lấy hình ảnh từ cache hoặc tải từ đường dẫn tệp nếu không có trong cache.
     */
    public static Image getImage(String path) {
        return cache.computeIfAbsent(path, key -> {
            try {
                if (key.startsWith("http") || key.startsWith("file:")) {
                    return new Image(key, true);  // Tải từ URL hoặc file path
                } else {
                    return new Image("file:" + key, true);  // Thêm "file:" nếu cần
                }
            } catch (Exception e) {
                System.out.println("Lỗi khi tải ảnh từ: " + key);
                e.printStackTrace();
                return null;  // Nếu có lỗi trả về null
            }
        });
    }

    /**
     * Tải hình ảnh từ cơ sở dữ liệu song song và lưu vào cache.
     */
    public void loadImagesFromDatabase() {
        String sql = "SELECT ISBN, imagePath FROM books";  // Câu truy vấn lấy ISBN và đường dẫn hình ảnh

        executorService.submit(() -> {
            try (Connection connection = getConnection();
                 Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(sql)) {

                while (resultSet.next()) {
                    String imagePath = resultSet.getString("imagePath");
                    String isbn = resultSet.getString("ISBN");  // Sử dụng ISBN làm khóa

                    if (imagePath != null && !imagePath.isEmpty()) {
                        executorService.submit(() -> loadImageToCache(imagePath, isbn));
                    }
                }

            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                executorService.shutdown();  // Đóng ExecutorService sau khi hoàn thành
            }
        });
    }

    /**
     * Tải hình ảnh từ đường dẫn file hoặc URL và lưu vào cache.
     */
    private void loadImageToCache(String imagePath, String isbn) {
        try {
            Image image = new Image(imagePath.startsWith("http") || imagePath.startsWith("file:")
                    ? imagePath
                    : "file:" + imagePath, true);
            cache.put("image_" + isbn, image);  // Lưu hình ảnh vào cache với khóa là ISBN
        } catch (Exception e) {
            System.out.println("Lỗi khi tải ảnh từ đường dẫn: " + imagePath + " (ISBN: " + isbn + ")");
            e.printStackTrace();
        }
    }


    /**
     * Tải hình ảnh từ đường dẫn file hoặc URL và lưu vào cache.
     */
    private void loadImageToCache(String imagePath, int bookId) {
        try {
            Image image = new Image(imagePath.startsWith("http") || imagePath.startsWith("file:")
                    ? imagePath
                    : "file:" + imagePath, true);
            cache.put("image_" + bookId, image);
        } catch (Exception e) {
            System.out.println("Lỗi khi tải ảnh từ đường dẫn: " + imagePath + " (ID: " + bookId + ")");
            e.printStackTrace();
        }
    }

    /**
     * Tải hình ảnh từ dữ liệu BLOB và lưu vào cache.
     */
    private void loadImageFromBlob(byte[] imageData, int bookId) {
        try {
            Image image = new Image(new ByteArrayInputStream(imageData));
            cache.put("image_" + bookId, image);
        } catch (Exception e) {
            System.out.println("Lỗi khi tải ảnh từ BLOB cho sách ID: " + bookId);
            e.printStackTrace();
        }
    }
}
